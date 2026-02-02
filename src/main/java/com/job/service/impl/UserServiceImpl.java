package com.job.service.impl;

import com.job.config.UploadProperties;
import com.job.entity.User;
import com.job.mapper.UserMapper;
import com.job.service.UserService;
import com.job.utils.AvatarFileUtil;
import com.job.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AvatarFileUtil avatarFileUtil;
    @Autowired
    private UploadProperties uploadProperties;

    /**
     * 更新用户头像
     */
    @Transactional
    public boolean updateUserAvatar(Long userId, MultipartFile avatarFile, HttpServletRequest request) throws IOException {
        User user = getUserByRole(userId);
        if (user == null) {
            return false;
        }

        // 删除旧头像文件
        if (user.getAvatar() != null) {
            avatarFileUtil.deleteAvatar(user.getAvatar());
        }

        // 上传新头像
        String newAvatarPath = avatarFileUtil.uploadAvatar(avatarFile);

        // 更新数据库
        user.setAvatar(newAvatarPath);
        user.setUpdatedAt(LocalDateTime.now());
        return userMapper.updateById(user) > 0;
    }

    /**
     * 删除用户头像
     */
    @Transactional
    public boolean deleteUserAvatar(Long userId, HttpServletRequest request) {
        User user = getUserByRole(userId);
        if (user == null || user.getAvatar() == null) {
            return false;
        }

        // 删除文件
        boolean fileDeleted = avatarFileUtil.deleteAvatar(user.getAvatar());

        // 更新数据库
        user.setAvatar(uploadProperties.getDefaultAvatar());
        user.setUpdatedAt(LocalDateTime.now());
        boolean dbUpdated = userMapper.updateById(user) > 0;

        return fileDeleted && dbUpdated;
    }


    public User getUserByRole(Long userId) {
        User user = null;
        if (SecurityUtils.isAdmin()) {
            // 获取用户当前头像路径
            user = userMapper.selectById(userId);
        } else {
            // 非管理员，只能更新自己的头像
            user = userMapper.selectById(SecurityUtils.getCurrentUser().getId());
        }
        return user;
    }

    /**
     * 获取用户头像文件
     */
    public File getUserAvatarFile(Long userId, HttpServletRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getAvatar() == null) {
            return getDefaultAvatarFile();
        }

        File avatarFile = avatarFileUtil.getAvatarFile(user.getAvatar());
        return avatarFile.exists() ? avatarFile : getDefaultAvatarFile();
    }

    private File getDefaultAvatarFile() {
        return avatarFileUtil.getDefaultAvatarFile();
    }
}
