package com.job.service;

import java.util.Map;
import com.job.common.page.PageResult;
import com.job.entity.User;
import com.job.entity.CompanyProfile;

public interface AdminService {
    /**
     * 获取管理后台统计数据
     * @return 包含用户总数、企业总数、职位总数、简历总数的统计信息
     */
    Map<String, Long> getDashboardStats();
    
    /**
     * 分页获取用户列表
     * @param username 用户名关键字
     * @param phone 手机号
     * @param status 状态（1-启用，0-禁用）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页用户列表
     */
    PageResult<User> getUserList(String username, String phone, Integer status, int page, int pageSize);
    
    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 新状态（1-启用，0-禁用）
     */
    void updateUserStatus(Long userId, Integer status);
    
    /**
     * 删除用户
     * @param userId 用户ID
     */
    void deleteUser(Long userId);
    
    /**
     * 重置用户密码
     * @param userId 用户ID
     * @return 新密码
     */
    String resetUserPassword(Long userId);

    /**
     * 分页获取企业列表（支持名称模糊、状态筛选，未认证排前）
     */
    PageResult<CompanyProfile> getCompanyList(String name, Integer status, int page, int pageSize);

    /**
     * 修改企业认证状态
     */
    void verifyCompany(Long id, Integer status);

    /**
     * 获取企业详情
     */
    CompanyProfile getCompanyById(Long id);

    /**
     * 获取企业关联用户
     */
    User getUserByCompanyId(Long companyId);

    /**
     * 用户增长趋势
     */
    java.util.List<java.util.Map<String, Object>> getUserGrowthTrend();

    /**
     * 职位投递趋势
     */
    java.util.List<java.util.Map<String, Object>> getJobApplicationTrend();

    /**
     * 企业入驻统计
     */
    java.util.List<java.util.Map<String, Object>> getCompanyPie();

    /**
     * 简历投递趋势
     */
    java.util.List<java.util.Map<String, Object>> getResumeTrend();
}
