package com.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.job.entity.User;
import com.job.entity.StudentProfile;
import com.job.entity.CompanyProfile;
import com.job.mapper.UserMapper;
import com.job.mapper.StudentProfileMapper;
import com.job.mapper.CompanyProfileMapper;
import com.job.security.JwtTokenProvider;
import com.job.security.UserPrincipal;
import com.job.service.AuthService;
import com.job.common.api.ApiResult;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
class UserAuthDTO {
    private Long id;
    private String username;
    private String role;
    private Long studentId;
    private Long companyId;
    private Long adminId;
    private Boolean verified;
    private String status;
}

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StudentProfileMapper studentProfileMapper;
    @Autowired
    private CompanyProfileMapper companyProfileMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private UserAuthDTO convertToDTO(User user) {
        UserAuthDTO dto = new UserAuthDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        
        if ("company".equals(user.getRole())) {
            CompanyProfile company = companyProfileMapper.selectOne(
                new QueryWrapper<CompanyProfile>().eq("user_id", user.getId())
            );
            dto.setCompanyId(company != null ? company.getId() : null);
            dto.setVerified(company != null && company.getVerificationStatus() != null && company.getVerificationStatus() == 1);
        } else if ("student".equals(user.getRole())) {
            StudentProfile student = studentProfileMapper.selectOne(
                new QueryWrapper<StudentProfile>().eq("user_id", user.getId())
            );
            dto.setStudentId(student != null ? student.getId() : null);
        } else if ("admin".equals(user.getRole())) {
            dto.setAdminId(user.getId());
        }
        
        return dto;
    }

    @Override
    @Transactional
    public ApiResult<?> register(User user) {
        // 1. 检查用户名是否存在
        if (userMapper.selectOne(new QueryWrapper<User>().eq("username", user.getUsername())) != null) {
            return ApiResult.failed("用户名已存在");
        }

        // 2. 设置用户创建时间和密码加密
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 3. 插入用户数据
        userMapper.insert(user);

        // 4. 根据角色创建对应的档案
        if ("student".equals(user.getRole())) {
            StudentProfile studentProfile = new StudentProfile();
            studentProfile.setUserId(user.getId());
            studentProfileMapper.insert(studentProfile);
        } else if ("company".equals(user.getRole())) {
            CompanyProfile companyProfile = new CompanyProfile();
            companyProfile.setUserId(user.getId());
            companyProfile.setVerificationStatus(0);
            companyProfile.setAuditStatus("pending");
            companyProfile.setCompanyName("未命名企业" + user.getId());
            companyProfileMapper.insert(companyProfile);
        }

        // 5. 返回成功结果，包含转换后的DTO
        Map<String, Object> data = new HashMap<>();
        data.put("user", convertToDTO(user));
        return ApiResult.success("注册成功", data);
    }

    @Override
    public ApiResult<?> login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            if (user.getStatus() != null && !"1".equals(user.getStatus().toString())) {
                return ApiResult.failed("账号已被禁用，请联系管理员");
            }
            String token = jwtTokenProvider.generateToken(UserPrincipal.create(user));
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", convertToDTO(user));
            return ApiResult.success("登录成功", data);
        }
        return ApiResult.failed("用户名或密码错误");
    }

    @Override
    public ApiResult<?> getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            return ApiResult.success("获取用户信息成功", user);
        }
        return ApiResult.failed("用户不存在");
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }
} 