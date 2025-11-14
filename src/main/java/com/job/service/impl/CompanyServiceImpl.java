package com.job.service.impl;

import com.job.common.page.PageResult;
import com.job.dto.CompanyWithSimpleDTO;
import com.job.entity.CompanyProfile;
import com.job.mapper.CompanyProfileMapper;
import com.job.mapper.JobMapper;
import com.job.mapper.ApplicationMapper;
import com.job.service.CompanyService;
import com.job.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private CompanyProfileMapper companyProfileMapper;
    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private ApplicationMapper applicationMapper;
    @Autowired
    private NotificationService notificationService;

    @Override
    public PageResult<CompanyWithSimpleDTO> getCompaniesWithSimple(CompanyProfile company, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<CompanyWithSimpleDTO> records = companyProfileMapper.selectCompaniesWithSimple(company, offset, pageSize);
        int total = companyProfileMapper.countCompaniesWithSimple(company);
        return new PageResult<>(records, total, page, pageSize);
    }
    @Override
    public CompanyProfile getById(Long id) {
        return companyProfileMapper.selectById(id);
    }

    @Override
    public Map<String, Object> getDashboardInfoWithNotification(Long companyId) {
        Map<String, Object> result = new HashMap<>();
        CompanyProfile company = companyProfileMapper.selectById(companyId);
        if (company == null) return result;
        // 统计数据
        int jobCount = jobMapper.countByCompanyId(companyId);
        java.util.List<com.job.entity.Job> jobs = jobMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.job.entity.Job>().eq("company_id", companyId)
        );
        java.util.List<Long> jobIds = jobs.stream().map(com.job.entity.Job::getId).collect(java.util.stream.Collectors.toList());
        int resumeCount = 0;
        if (!jobIds.isEmpty()) {
            resumeCount = applicationMapper.countByJobIds(jobIds);
        }
        // 未读消息数
        int notificationCount = notificationService.getUnreadCount(company.getUserId());
        // 组装
        result.put("companyName", company.getCompanyName());
        result.put("logoUrl", company.getLogoUrl());
        result.put("industry", company.getIndustry());
        result.put("verificationStatus", company.getVerificationStatus());
        result.put("jobCount", jobCount);
        result.put("resumeCount", resumeCount);
        result.put("notificationCount", notificationCount);
        return result;
    }

    @Override
    public boolean updateCompanyDetail(CompanyProfile profile) {
        int result = companyProfileMapper.updateById(profile);
        return result > 0;
    }





} 