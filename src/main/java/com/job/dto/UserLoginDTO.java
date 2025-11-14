package com.job.dto;

import lombok.Data;

@Data
public class UserLoginDTO {
    private Long id;           // 用户表主键
    private String username;
    private String role;
    private Long companyId;    // 企业用户时有值
    private Long studentId;    // 学生用户时有值
    private Long adminId;      // 管理员时有值
    private String status;
} 