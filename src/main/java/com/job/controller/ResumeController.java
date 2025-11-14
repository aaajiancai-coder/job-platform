package com.job.controller;

import com.job.common.api.ApiResult;
import com.job.common.page.PageResult;
import com.job.dto.ApplicationWithJobCompanyDTO;
import com.job.dto.ApplicationDetailDTO;
import com.job.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
public class ResumeController {
    @Autowired
    private ApplicationService applicationService;

    // 分页获取公司收到的简历
    @GetMapping("/resumes")
    public ApiResult<PageResult<ApplicationWithJobCompanyDTO>> getCompanyResumes(
            @RequestParam Long companyId,
            @RequestParam(required = false) Long jobId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return ApiResult.success(applicationService.getCompanyResumes(companyId, jobId, status, page, pageSize));
    }

    // 更新简历状态
    @PutMapping("/resume/{applicationId}/status")
    public ApiResult<?> updateResumeStatus(@PathVariable Long applicationId, @RequestBody ApplicationWithJobCompanyDTO dto) {
        applicationService.updateResumeStatus(applicationId, dto.getStatus());
        return ApiResult.success();
    }

    @GetMapping("/resume/{applicationId}/detail")
    public ApiResult<ApplicationDetailDTO> getApplicationDetail(@PathVariable Long applicationId) {
        return ApiResult.success(applicationService.getApplicationDetail(applicationId));
    }

    // 发送offer接口
    @PostMapping("/resume/{applicationId}/send-offer")
    public ApiResult<?> sendOffer(@PathVariable Long applicationId) {
        boolean result = applicationService.sendOffer(applicationId);
        if (result) {
            return ApiResult.success();
        } else {
            return ApiResult.failed("只有已通过状态才能发送offer");
        }
    }
} 