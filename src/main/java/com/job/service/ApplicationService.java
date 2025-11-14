package com.job.service;

import com.job.dto.ApplicationWithJobCompanyDTO;
import com.job.common.page.PageResult;
import com.job.dto.ApplicationDetailDTO;

public interface ApplicationService {
    PageResult<ApplicationWithJobCompanyDTO> getApplicationList(Long studentId, int page, int pageSize);
    void withdrawApplication(Long applicationId);
    void applyJob(Long jobId, Long resumeId, Long userId);
    PageResult<ApplicationWithJobCompanyDTO> getCompanyResumes(Long userId, Long jobId, String status, int page, int pageSize);
    void updateResumeStatus(Long applicationId, String status);
    ApplicationDetailDTO getApplicationDetail(Long applicationId);
    boolean sendOffer(Long applicationId);
} 