package com.job.utils;

import com.job.entity.User;
import com.job.security.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {

    //获取当前登录用户信息
    public static UserPrincipal getCurrentUser() {
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    //判断是否为管理员
    public static boolean isAdmin() {
        UserDetails userDetails = getCurrentUser();
        return userDetails != null && userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
