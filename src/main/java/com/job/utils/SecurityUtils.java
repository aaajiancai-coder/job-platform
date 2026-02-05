package com.job.utils;

import com.job.entity.User;
import com.job.security.UserPrincipal;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
@Slf4j
public class SecurityUtils {

    //获取当前登录用户信息
    public static UserPrincipal getCurrentUser() {
        // 1. 获取 Authentication 对象（先判空）
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.info("【获取当前用户】- 无认证信息，返回 null");
            return null; // 无认证信息，返回 null 而非抛异常
        }

        // 2. 判断是否是匿名认证
        if (authentication instanceof AnonymousAuthenticationToken) {
            log.info("【获取当前用户】- 匿名用户，principal：{}", authentication.getPrincipal());
            return null; // 匿名用户，返回 null
        }

        // 3. 获取 Principal 并判断类型（避免强转异常）
        Object principal = authentication.getPrincipal();
        log.info("【获取当前用户】- principal 类型：{}，值：{}",
                principal.getClass().getName(), principal);

        if (principal instanceof UserPrincipal) {
            return (UserPrincipal) principal; // 类型匹配，安全转换
        } else {
            // 类型不匹配（如 String），打印日志并返回 null
            log.warn("【获取当前用户】- Principal 类型不匹配，期望 UserPrincipal，实际：{}",
                    principal.getClass().getName());
            return null;
        }
    }

    //判断是否为管理员
    public static boolean isAdmin() {
        UserDetails userDetails = getCurrentUser();
        return userDetails != null && userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
