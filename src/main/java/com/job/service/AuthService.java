package com.job.service;

import com.job.entity.User;
import com.job.common.api.ApiResult;

public interface AuthService {
    ApiResult<?> register(User user);
    ApiResult<?> login(String username, String password);
    ApiResult<?> getCurrentUser(Long userId);
    User getUserById(Long userId);
} 