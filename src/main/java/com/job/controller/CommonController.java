package com.job.controller;

import com.job.dto.JobSimpleDTO;
import com.job.dto.CompanySimpleDTO;
import com.job.dto.JobWithCompanyDTO;
import com.job.dto.CompanyWithSimpleDTO;
import com.job.service.CommonService;
import com.job.service.JobService;
import com.job.service.CompanyService;
import com.job.common.api.ApiResult;
import com.job.common.page.PageResult;
import com.job.entity.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/common")
public class CommonController {
    @Autowired
    private CommonService commonService;
    @Autowired
    private JobService jobService;
    @Autowired
    private CompanyService companyService;

    @GetMapping("/hot-jobs")
    public ApiResult<List<JobSimpleDTO>> getHotJobs() {
        return ApiResult.success(commonService.getRandomJobs(3));
    }

    @GetMapping("/hot-companies")
    public ApiResult<List<CompanySimpleDTO>> getHotCompanies() {
        return ApiResult.success(commonService.getRandomCompanies(3));
    }

    // 新增：职位分页查询，支持Job实体动态参数
    @GetMapping("/jobs")
    public ApiResult<PageResult<JobWithCompanyDTO>> getJobs(
            @ModelAttribute Job job,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int pageSize
    ) {
        return ApiResult.success(jobService.getJobsByParams(job.getTitle(), job.getLocation(), job.getJobType(), page, pageSize));
    }

    // 新增：公司分页查询，支持CompanyProfile实体动态参数
    @GetMapping("/companies")
    public ApiResult<PageResult<CompanyWithSimpleDTO>> getCompanies(
            @ModelAttribute com.job.entity.CompanyProfile company,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int pageSize
    ) {
        return ApiResult.success(companyService.getCompaniesWithSimple(company, page, pageSize));
    }
}
