package com.job.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface UserService {
    boolean updateUserAvatar(Long userId, MultipartFile avatarFile) throws IOException;

    boolean deleteUserAvatar(Long userId);

    File getUserAvatarFile(Long userId);
}
