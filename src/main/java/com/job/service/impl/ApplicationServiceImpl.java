package com.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.job.dto.ApplicationDetailDTO;
import com.job.entity.CompanyProfile;
import com.job.mapper.ApplicationMapper;
import com.job.mapper.CompanyProfileMapper;
import com.job.service.ApplicationService;
import com.job.dto.ApplicationWithJobCompanyDTO;
import com.job.common.page.PageResult;
import com.job.mapper.StudentProfileMapper;
import com.job.entity.StudentProfile;
import com.job.entity.Application;
import com.job.entity.Resume;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.job.mapper.ResumeMapper;
import com.job.service.NotificationService;
import com.job.mapper.JobMapper;
import com.job.entity.Job;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {
    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private StudentProfileMapper studentProfileMapper;

    @Autowired
    private ResumeMapper resumeMapper;

    @Autowired
    private CompanyProfileMapper companyProfileMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JobMapper jobMapper;


    @Override
    public PageResult<ApplicationWithJobCompanyDTO> getApplicationList(Long studentId, int page, int pageSize) {
        Assert.notNull(studentId, "学生ID不能为空");
        Assert.isTrue(page > 0, "页码必须大于0");
        Assert.isTrue(pageSize > 0, "每页大小必须大于0");

        int offset = (page - 1) * pageSize;
        List<ApplicationWithJobCompanyDTO> records = applicationMapper.selectWithJobAndCompanyByStudentIdPaged(studentId, offset, pageSize);
        int total = applicationMapper.countByStudentId(studentId);
        return new PageResult<>(records, total, page, pageSize);
    }

    @Override
    @Transactional
    public void withdrawApplication(Long applicationId) {
        Assert.notNull(applicationId, "申请ID不能为空");
        
        Application application = applicationMapper.selectById(applicationId);
        Assert.notNull(application, "未找到对应的申请记录");
        Assert.isTrue("待处理".equals(application.getStatus()), "只有待处理状态的申请可以撤回");
        
        applicationMapper.withdrawById(applicationId);
    }

    @Override
    @Transactional
    public void applyJob(Long jobId, Long resumeId, Long userId) {
        Assert.notNull(jobId, "职位ID不能为空");
        Assert.notNull(resumeId, "简历ID不能为空");
        Assert.notNull(userId, "用户ID不能为空");

        // 验证职位是否存在
        Job job = jobMapper.selectById(jobId);
        Assert.notNull(job, "职位不存在");

        // 验证简历是否存在
        Resume resume = resumeMapper.selectById(resumeId);
        Assert.notNull(resume, "简历不存在");

        StudentProfile profile = studentProfileMapper.selectByUserId(userId);
        Assert.notNull(profile, "学生信息不存在");

        // 检查是否已经申请过该职位
        QueryWrapper<Application> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("job_id", jobId)
                   .eq("student_id", profile.getId());
        Long count = applicationMapper.selectCount(queryWrapper);
        Assert.isTrue(count == 0, "您已经申请过该职位");

        Application app = new Application();
        app.setJobId(jobId);
        app.setResumeId(resumeId);
        app.setStudentId(profile.getId());
        app.setStatus("待处理");
        app.setApplyTime(new Date());
        applicationMapper.insert(app);

        // 新增：向企业发送消息
        CompanyProfile company = companyProfileMapper.selectById(job.getCompanyId());
        String content = profile.getRealName() + "向贵公司【" + company.getCompanyName() + "】的岗位【" + job.getTitle() + "】投递了简历。";
        notificationService.sendNotification(
            company.getUserId(), // 接收方为企业用户
            profile.getUserId(), // 发送方为学生
            app.getId(),
            "简历投递",
            content
        );
    }

    @Override
    public PageResult<ApplicationWithJobCompanyDTO> getCompanyResumes(Long companyId, Long jobId, String status, int page, int pageSize) {
        Assert.notNull(companyId, "公司ID不能为空");
        Assert.isTrue(page > 0, "页码必须大于0");
        Assert.isTrue(pageSize > 0, "每页大小必须大于0");

        int offset = (page - 1) * pageSize;
        List<ApplicationWithJobCompanyDTO> records = applicationMapper.selectCompanyResumes(companyId, jobId, status, offset, pageSize);
        int total = applicationMapper.countCompanyResumes(companyId, jobId, status);
        return new PageResult<>(records, total, page, pageSize);
    }

    @Override
    @Transactional
    public void updateResumeStatus(Long applicationId, String status) {
        Assert.notNull(applicationId, "申请ID不能为空");
        Assert.hasText(status, "状态不能为空");

        Application application = applicationMapper.selectById(applicationId);
        Assert.notNull(application, "未找到对应的申请记录");

        // 验证状态转换的合法性
        validateStatusTransition(application.getStatus(), status);
        
        applicationMapper.updateResumeStatus(applicationId, status);
        
        // 获取必要的相关数据
        Job job = jobMapper.selectById(application.getJobId());
        Assert.notNull(job, "职位信息不存在");
        
        CompanyProfile company = companyProfileMapper.selectById(job.getCompanyId());
        Assert.notNull(company, "公司信息不存在");

        // 发送通知
        sendStatusChangeNotification(application, job, company, status);
    }

    private void validateStatusTransition(String currentStatus, String newStatus) {
        // 定义状态转换规则
        if ("已发送offer".equals(currentStatus)) {
            throw new IllegalArgumentException("已发送offer的申请不能更改状态");
        }
        if ("已拒绝".equals(currentStatus)) {
            throw new IllegalArgumentException("已拒绝的申请不能更改状态");
        }
        if ("已通过".equals(currentStatus) && !"已发送offer".equals(newStatus)) {
            throw new IllegalArgumentException("已通过的申请只能发送offer");
        }
    }

    private void sendStatusChangeNotification(Application application, Job job, CompanyProfile company, String status) {
        String content;
        String type;
        
        switch (status) {
            case "已读":
                content = "你的简历已被【" + company.getCompanyName() + "】的职位【" + job.getTitle() + "】查看。";
                type = "状态更新";
                break;
            case "已邀请面试":
                content = "【" + company.getCompanyName() + "】的职位【" + job.getTitle() + "】邀请你进行面试，请尽快回复。";
                type = "面试邀请";
                break;
            case "已拒绝":
                content = "【" + company.getCompanyName() + "】的职位【" + job.getTitle() + "】已拒绝你的简历。";
                type = "反馈";
                break;
            case "已通过":
                content = "【" + company.getCompanyName() + "】的职位【" + job.getTitle() + "】已通过你的简历，请等待后续通知。";
                type = "反馈";
                break;
            case "已发送offer":
                content = "【" + company.getCompanyName() + "】的职位【" + job.getTitle() + "】向你发送了offer！";
                type = "offer";
                break;
            default:
                content = "【" + company.getCompanyName() + "】的职位【" + job.getTitle() + "】状态已更新为【" + status + "】。";
                type = "状态更新";
        }

        notificationService.sendNotification(
            application.getStudentId(),
            company.getUserId(),
            application.getId(),
            type,
            content
        );
    }

    @Override
    @Transactional
    public boolean sendOffer(Long applicationId) {
        Assert.notNull(applicationId, "申请ID不能为空");

        Application application = applicationMapper.selectById(applicationId);
        Assert.notNull(application, "未找到对应的申请记录");
        
        if (!"已通过".equals(application.getStatus())) {
            return false;
        }

        Job job = jobMapper.selectById(application.getJobId());
        Assert.notNull(job, "职位信息不存在");

        CompanyProfile company = companyProfileMapper.selectById(job.getCompanyId());
        Assert.notNull(company, "公司信息不存在");

        application.setStatus("已发送offer");
        applicationMapper.updateById(application);

        String content = "【" + company.getCompanyName() + "】的职位【" + job.getTitle() + "】向你发送了offer！";
        notificationService.sendNotification(
            application.getStudentId(),
            company.getUserId(),
            applicationId,
            "offer",
            content
        );
        return true;
    }

    @Override
    public ApplicationDetailDTO getApplicationDetail(Long applicationId) {
        Assert.notNull(applicationId, "申请ID不能为空");

        ApplicationDetailDTO dto = new ApplicationDetailDTO();
        Application application = applicationMapper.selectById(applicationId);
        Assert.notNull(application, "未找到对应的申请记录");

        dto.setApplication(application);
        
        StudentProfile student = studentProfileMapper.selectById(application.getStudentId());
        Assert.notNull(student, "未找到对应的学生信息");
        dto.setStudent(student);

        if (application.getResumeId() != null) {
            Resume resume = resumeMapper.selectById(application.getResumeId());
            if (resume != null) {
                dto.setResume(resume);
            }
        }

        return dto;
    }
} 