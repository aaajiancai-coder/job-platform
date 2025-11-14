package com.job.controller;

import com.job.dto.StudentDashboardDTO;
import com.job.common.api.ApiResult;
import com.job.service.StudentService;
import com.job.service.ResumeService;
import com.job.entity.Resume;
import com.job.service.ApplicationService;
import com.job.dto.ApplicationWithJobCompanyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.job.common.page.PageResult;
import com.job.dto.ApplyRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.job.security.UserPrincipal;
import com.job.common.exception.ServiceException;
import com.job.common.exception.UnauthorizedException;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private ApplicationService applicationService;


    @GetMapping("/dashboard")
    public ApiResult<StudentDashboardDTO> getDashboardInfo(@RequestParam Long studentId) {
        return ApiResult.success(studentService.getDashboardInfo(studentId));
    }

    // 获取简历列表
    @GetMapping("/resumes")
    public ApiResult<PageResult<Resume>> getResumeList(
        @RequestParam Long studentId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        return ApiResult.success(resumeService.getResumeList(studentId, page, pageSize));
    }

    // 新建简历
    @PostMapping("/resume")
    public ApiResult<Void> createResume(@RequestBody Resume resume) {
        resumeService.createResume(resume);
        return ApiResult.success(null);
    }

    // 编辑简历
    @PutMapping("/resume")
    public ApiResult<Void> updateResume(@RequestBody Resume resume) {
        resumeService.updateResume(resume);
        return ApiResult.success(null);
    }

    // 删除简历
    @DeleteMapping("/resume/{resumeId}")
    public ApiResult<Void> deleteResume(@PathVariable Long resumeId) {
        resumeService.deleteResume(resumeId);
        return ApiResult.success(null);
    }

    // 获取投递记录列表
    @GetMapping("/applications")
    public ApiResult<PageResult<ApplicationWithJobCompanyDTO>> getApplicationList(
        @RequestParam Long studentId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        return ApiResult.success(applicationService.getApplicationList(studentId, page, pageSize));
    }

    // 撤回投递
    @PostMapping("/application/{applicationId}/withdraw")
    public ApiResult<Void> withdrawApplication(@PathVariable Long applicationId) {
        applicationService.withdrawApplication(applicationId);
        return ApiResult.success(null);
    }

    @PostMapping("/apply")
    public ApiResult<Void> applyJob(@RequestBody ApplyRequest req) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("未登录或登录已过期");
        }
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        boolean isStudent = userPrincipal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_student"));
        if (!isStudent) {
            throw new ServiceException("只有学生可以投递简历");
        }
        applicationService.applyJob(req.getJobId(), req.getResumeId(), userPrincipal.getId());
        return ApiResult.success(null);
    }

    // 获取学生详细信息
    @GetMapping("/{id}")
    public ApiResult<?> getStudentDetail(@PathVariable Long id) {
        return ApiResult.success(studentService.getStudentDetail(id));
    }

    // 修改学生详细信息
    @PutMapping("/{id}")
    public ApiResult<?> updateStudentDetail(@PathVariable Long id, @RequestBody com.job.entity.StudentProfile profile) {
        profile.setId(id);
        boolean success = studentService.updateStudentDetail(profile);
        return success ? ApiResult.success() : ApiResult.failed("修改失败");
    }
}