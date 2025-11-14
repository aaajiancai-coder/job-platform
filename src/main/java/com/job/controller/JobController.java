package com.job.controller;

import com.job.dto.JobWithCompanyDTO;
import com.job.common.api.ApiResult;
import com.job.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
public class JobController {
    @Autowired
    private JobService jobService;

    @GetMapping("/{id}")
    public ApiResult<JobWithCompanyDTO> getJobDetail(@PathVariable Long id) {
        JobWithCompanyDTO job = jobService.getJobDetail(id);
        if (job == null) {
            return ApiResult.failed("职位不存在");
        }
        return ApiResult.success(job);
    }


} 