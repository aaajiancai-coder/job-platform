package com.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

import java.util.Arrays;
import java.util.List;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    private String password;
    private String email;
    private String phone;
    
    @TableField("role")
    private String role;
    
    private String avatar;
    
    @TableField(exist = false)
    private List<String> roles;
    
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public List<String> getRoles() {
        return Arrays.asList(role == null ? "" : role.trim().toLowerCase());
    }

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}