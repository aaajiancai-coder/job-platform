package com.job.controller;

import com.job.common.page.PageResult;
import com.job.entity.CompanyProfile;
import com.job.common.api.ApiResult;
import com.job.entity.Job;
import com.job.service.CompanyService;
import com.job.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/company")
public class CompanyController {
    @Autowired
    private CompanyService companyService;
    @Autowired
    private JobService jobService;


    @GetMapping("/{id}")
    public ApiResult<CompanyProfile> getCompanyDetail(@PathVariable Long id) {
        CompanyProfile company = companyService.getById(id);
        if (company == null) {
            return ApiResult.failed("公司不存在");
        }
        return ApiResult.success(company);
    }

    @GetMapping("/dashboard")
    public ApiResult<Map<String, Object>> getDashboard(@RequestParam Long companyId) {
        Map<String, Object> result = companyService.getDashboardInfoWithNotification(companyId);
        return ApiResult.success(result);
    }

    // 企业端：分页获取本公司职位列表
    @GetMapping("/jobs")
    public ApiResult<PageResult<Job>> getCompanyJobs(@RequestParam Long companyId,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<Job> result = jobService.getJobsByCompany(companyId, page, pageSize);
        return ApiResult.success(result);
    }

    // 企业端：新增职位
    @PostMapping("/jobs")
    public ApiResult<?> createJob(@RequestBody Job job) {
        boolean success = jobService.createJob(job);
        return success ? ApiResult.success() : ApiResult.failed("新增失败");
    }

    // 企业端：编辑职位
    @PutMapping("/jobs/{id}")
    public ApiResult<?> updateJob(@PathVariable("id") Long id, @RequestBody Job job) {
        job.setId(id);
        boolean success = jobService.updateJob(job);
        return success ? ApiResult.success() : ApiResult.failed("修改失败");
    }

    // 企业端：删除职位
    @DeleteMapping("jobs/{id}")
    public ApiResult<?> deleteJob(@PathVariable Long id) {
        boolean success = jobService.deleteJob(id);
        return success ? ApiResult.success() : ApiResult.failed("删除失败");
    }

    // 企业端：上下架职位
    @PutMapping("/jobs/{id}/status")
    public ApiResult<?> changeJobStatus(@PathVariable Long id, @RequestBody Job job) {
        boolean success = jobService.changeJobStatus(id, job.getStatus());
        return success ? ApiResult.success() : ApiResult.failed("操作失败");
    }

    @PutMapping("/{id}")
    public ApiResult<?> updateCompanyDetail(@PathVariable Long id, @RequestBody CompanyProfile profile) {
        profile.setId(id);
        boolean success = companyService.updateCompanyDetail(profile);
        return success ? ApiResult.success() : ApiResult.failed("修改失败");
    }

}
