package com.job.service.impl;

import com.job.entity.User;
import com.job.mapper.UserMapper;
import com.job.service.UserService;
import com.job.utils.AvatarFileUtil;
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

    /**
     * 更新用户头像
     */
    @Transactional
    public boolean updateUserAvatar(Long userId, MultipartFile avatarFile, HttpServletRequest request) {
        try {
            // 获取用户当前头像路径
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new IllegalArgumentException("用户不存在");
            }

            // 删除旧头像文件
            if (user.getAvatar() != null) {
                AvatarFileUtil.deleteAvatar(user.getAvatar(), request);
            }

            // 上传新头像
            String newAvatarPath = AvatarFileUtil.uploadAvatar(avatarFile, request);

            // 更新数据库
            user.setAvatar(newAvatarPath);
            user.setUpdatedAt(LocalDateTime.now());
            return userMapper.updateById(user) > 0;

        } catch (IOException e) {
            throw new RuntimeException("头像更新失败", e);
        }
    }

    /**
     * 删除用户头像
     */
    @Transactional
    public boolean deleteUserAvatar(Long userId, HttpServletRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getAvatar() == null) {
            return false;
        }

        // 删除文件
        boolean fileDeleted = AvatarFileUtil.deleteAvatar(user.getAvatar(), request);

        // 更新数据库
        user.setAvatar(null);
        user.setUpdatedAt(LocalDateTime.now());
        boolean dbUpdated = userMapper.updateById(user) > 0;

        return fileDeleted && dbUpdated;
    }

    /**
     * 获取用户头像文件
     */
    public File getUserAvatarFile(Long userId, HttpServletRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getAvatar() == null) {
            return getDefaultAvatarFile(request);
        }

        File avatarFile = AvatarFileUtil.getAvatarFile(user.getAvatar(), request);
        return avatarFile.exists() ? avatarFile : getDefaultAvatarFile(request);
    }

    private File getDefaultAvatarFile(HttpServletRequest request) {
        // 返回默认头像文件
        String defaultAvatarPath = "/images/default-avatar.png";
        return new File(request.getServletContext().getRealPath(defaultAvatarPath));
    }
}
