package com.job.controller;

import com.job.common.api.ApiResult;
import com.job.service.AdminService;
import com.job.common.page.PageResult;
import com.job.entity.User;
import com.job.entity.CompanyProfile;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Resource
    private AdminService adminService;
    
    @GetMapping("/dashboard/stats")
    public ApiResult<Map<String, Long>> getDashboardStats() {
        return ApiResult.success(adminService.getDashboardStats());
    }
    
    @GetMapping("/users")
    public ApiResult<PageResult<User>> getUserList(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PageResult<User> result = adminService.getUserList(
                username, phone, status, page, pageSize);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.failed(e.getMessage());
        }
    }
    
    @PutMapping("/users/{userId}/status")
    public ApiResult<Void> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam Integer status) {
        try {
            adminService.updateUserStatus(userId, status);
            return ApiResult.success();
        } catch (Exception e) {
            return ApiResult.failed(e.getMessage());
        }
    }
    
    @DeleteMapping("/users/{userId}")
    public ApiResult<Void> deleteUser(@PathVariable Long userId) {
        try {
            adminService.deleteUser(userId);
            return ApiResult.success();
        } catch (Exception e) {
            return ApiResult.failed(e.getMessage());
        }
    }
    
    @PostMapping("/users/{userId}/reset-password")
    public ApiResult<String> resetUserPassword(@PathVariable Long userId) {
        try {
            String newPassword = adminService.resetUserPassword(userId);
            return ApiResult.success(newPassword);
        } catch (Exception e) {
            return ApiResult.failed(e.getMessage());
        }
    }

    /**
     * 分页获取企业列表（支持名称模糊、状态筛选，未认证排前）
     */
    @GetMapping("/companies")
    public ApiResult<PageResult<CompanyProfile>> getCompanyList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResult.success(adminService.getCompanyList(name, status, page, pageSize));
    }

    /**
     * 修改企业认证状态
     */
    @PutMapping("/companies/{id}/verify")
    public ApiResult<Void> verifyCompany(@PathVariable Long id, @RequestParam Integer status) {
        adminService.verifyCompany(id, status);
        return ApiResult.success();
    }

    /**
     * 获取企业+用户详情
     */
    @GetMapping("/companies/{id}/detail")
    public ApiResult<Map<String, Object>> getCompanyDetail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        CompanyProfile company = adminService.getCompanyById(id);
        if (company == null) return ApiResult.failed("企业不存在");
        User user = adminService.getUserByCompanyId(id);
        result.put("company", company);
        result.put("user", user);
        return ApiResult.success(result);
    }

    /**
     * 用户增长趋势
     */
    @GetMapping("/stats/user-growth")
    public ApiResult<?> getUserGrowth() {
        return ApiResult.success(adminService.getUserGrowthTrend());
    }

    /**
     * 职位投递趋势
     */
    @GetMapping("/stats/job-application")
    public ApiResult<?> getJobApplicationTrend() {
        return ApiResult.success(adminService.getJobApplicationTrend());
    }

    /**
     * 企业入驻统计
     */
    @GetMapping("/stats/company-pie")
    public ApiResult<?> getCompanyPie() {
        return ApiResult.success(adminService.getCompanyPie());
    }

    /**
     * 简历投递趋势
     */
    @GetMapping("/stats/resume-trend")
    public ApiResult<?> getResumeTrend() {
        return ApiResult.success(adminService.getResumeTrend());
    }
}
