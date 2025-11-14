package com.job.service.impl;

import com.job.mapper.AdminMapper;
import com.job.service.AdminService;
import com.job.entity.User;
import com.job.common.page.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.job.entity.CompanyProfile;
import com.job.mapper.CompanyProfileMapper;
import com.job.mapper.UserMapper;
import com.job.mapper.JobMapper;
import com.job.mapper.ApplicationMapper;
import com.job.mapper.ResumeMapper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {
    
    @Resource
    private AdminMapper adminMapper;
    
    @Resource
    private PasswordEncoder passwordEncoder;
    
    @Resource
    private CompanyProfileMapper companyProfileMapper;
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private JobMapper jobMapper;
    
    @Resource
    private ApplicationMapper applicationMapper;
    
    @Resource
    private ResumeMapper resumeMapper;
    
    @Override
    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("userCount", adminMapper.countUsers());
        stats.put("companyCount", adminMapper.countCompanies());
        stats.put("jobCount", adminMapper.countJobs());
        stats.put("resumeCount", adminMapper.countResumes());
        return stats;
    }

    @Override
    public PageResult<User> getUserList(String username, String phone, Integer status, int page, int pageSize) {
        Assert.isTrue(page > 0, "页码必须大于0");
        Assert.isTrue(pageSize > 0, "每页大小必须大于0");

        int offset = (page - 1) * pageSize;
        List<User> records = adminMapper.selectUserList(username, phone, status, offset, pageSize);
        Long total = adminMapper.countUserList(username, phone, status);

        return new PageResult<>(records, total, page, pageSize);
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        Assert.notNull(userId, "用户ID不能为空");
        Assert.notNull(status, "状态不能为空");
        Assert.isTrue(status == 0 || status == 1, "状态值必须是0或1");
        
        int rows = adminMapper.updateUserStatus(userId, status);
        Assert.isTrue(rows > 0, "用户不存在或更新失败");
    }

    @Override
    public void deleteUser(Long userId) {
        if (userId == null) {
            return;
        }
        int rows = adminMapper.deleteUser(userId);
        Assert.isTrue(rows > 0, "用户不存在或删除失败");
    }
    
    @Override
    public String resetUserPassword(Long userId) {
        if  (userId == null) {
            return null;
        }
        
        // 生成8位随机密码
        String newPassword = UUID.randomUUID().toString().substring(0, 8);
        String encodedPassword = passwordEncoder.encode(newPassword);
        
        int rows = adminMapper.updateUserPassword(userId, encodedPassword);
        Assert.isTrue(rows > 0, "用户不存在或密码重置失败");
        
        return newPassword;
    }

    @Override
    public PageResult<CompanyProfile> getCompanyList(String name, Integer status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        // 构建动态SQL
        String sql = "SELECT * FROM company_profiles WHERE 1=1";
        String countSql = "SELECT COUNT(*) FROM company_profiles WHERE 1=1";
        StringBuilder where = new StringBuilder();
        if (name != null && !name.isEmpty()) {
            where.append(" AND company_name LIKE '%" + name + "%'");
        }
        if (status != null) {
            where.append(" AND verification_status = " + status);
        }
        // 未认证排前
        String order = " ORDER BY verification_status ASC, id DESC";
        List<CompanyProfile> records = companyProfileMapper.selectListRaw(sql + where + order + " LIMIT " + offset + "," + pageSize);
        Integer total = companyProfileMapper.countRaw(countSql + where);
        return new PageResult<>(records, total, page, pageSize);
    }

    @Override
    public void verifyCompany(Long id, Integer status) {
        CompanyProfile company = companyProfileMapper.selectById(id);
        if (company == null) throw new RuntimeException("企业不存在");
        company.setVerificationStatus(status);
        companyProfileMapper.updateById(company);
    }

    @Override
    public CompanyProfile getCompanyById(Long id) {
        return companyProfileMapper.selectById(id);
    }

    @Override
    public User getUserByCompanyId(Long companyId) {
        CompanyProfile company = companyProfileMapper.selectById(companyId);
        if (company == null) return null;
        return userMapper.selectById(company.getUserId());
    }

    @Override
    public java.util.List<java.util.Map<String, Object>> getUserGrowthTrend() {
        // 示例：近7天用户增长
        java.util.List<java.util.Map<String, Object>> list = new java.util.ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            int count = adminMapper.countUserByDate(date.format(fmt));
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("date", date.format(fmt));
            map.put("count", count);
            list.add(map);
        }
        return list;
    }

    @Override
    public java.util.List<java.util.Map<String, Object>> getJobApplicationTrend() {
        // 示例：近7天职位投递数
        java.util.List<java.util.Map<String, Object>> list = new java.util.ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            int count = applicationMapper.countByDate(date.format(fmt));
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("date", date.format(fmt));
            map.put("count", count);
            list.add(map);
        }
        return list;
    }

    @Override
    public java.util.List<java.util.Map<String, Object>> getCompanyPie() {
        // 示例：按行业统计企业数量
        return adminMapper.countCompanyByIndustry();
    }

    @Override
    public java.util.List<java.util.Map<String, Object>> getResumeTrend() {
        // 示例：近7天简历投递数
        java.util.List<java.util.Map<String, Object>> list = new java.util.ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            int count = resumeMapper.countByDate(date.format(fmt));
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("date", date.format(fmt));
            map.put("count", count);
            list.add(map);
        }
        return list;
    }
}
