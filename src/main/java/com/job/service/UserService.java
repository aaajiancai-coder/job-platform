package com.job.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface UserService {
    boolean updateUserAvatar(Long userId, MultipartFile avatarFile, HttpServletRequest request);

    boolean deleteUserAvatar(Long userId, HttpServletRequest request);

    File getUserAvatarFile(Long userId, HttpServletRequest request);
}
