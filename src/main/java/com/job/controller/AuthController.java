package com.job.controller;

import com.job.entity.User;
import com.job.service.AuthService;
import com.job.security.JwtTokenProvider;
import com.job.common.api.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ApiResult<?> register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public ApiResult<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        return authService.login(username, password);
    }

    @GetMapping("/me")
    public ApiResult<?> me(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtTokenProvider.getUserIdFromJWT(token);
        return authService.getCurrentUser(userId);
    }
}
