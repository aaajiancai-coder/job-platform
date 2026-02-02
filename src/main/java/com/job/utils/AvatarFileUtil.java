package com.job.utils;

import com.job.config.UploadProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 头像文件管理工具类（基于配置类版本）
 */
@Component
@EnableConfigurationProperties(UploadProperties.class)
public class AvatarFileUtil {

    private final UploadProperties uploadProperties;

    @Autowired
    public AvatarFileUtil(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
    }

    /**
     * 上传头像文件
     */
    public String uploadAvatar(MultipartFile file) throws IOException {
        // 验证文件
        validateFile(file);

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID() + fileExtension;

        // 创建存储目录
        Path uploadPath = Paths.get(uploadProperties.getBaseUrl(), uploadProperties.getAvatarSubDir());
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 保存文件
        Path filePath = uploadPath.resolve(newFilename);
        file.transferTo(filePath.toFile());

        // 返回相对路径或文件名
        return newFilename;
    }

    /**
     * 删除头像文件
     */
    public boolean deleteAvatar(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return true;
        }

        try {
            Path filePath = Paths.get(uploadProperties.getBaseUrl(), uploadProperties.getAvatarSubDir(), filename);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("删除头像文件失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取头像文件
     */
    public File getAvatarFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return getDefaultAvatarFile();
        }

        Path filePath = Paths.get(uploadProperties.getBaseUrl(), uploadProperties.getAvatarSubDir(), filename);
        File avatarFile = filePath.toFile();
        return avatarFile.exists() ? avatarFile : getDefaultAvatarFile();
    }

    /**
     * 验证文件合法性
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 使用配置中的文件大小限制
        if (uploadProperties.getMaxSize() != null &&
                file.getSize() > uploadProperties.getMaxSize()) {
            throw new IllegalArgumentException("文件大小不能超过配置限制");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename).toLowerCase();

        // 使用配置中的允许类型
        if (uploadProperties.getAllowTypes() != null &&
                !uploadProperties.getAllowTypes().isEmpty()) {
            boolean allowed = uploadProperties.getAllowTypes().stream()
                    .anyMatch(type -> type.equalsIgnoreCase(fileExtension));
            if (!allowed) {
                throw new IllegalArgumentException("文件类型不支持，允许的类型: " +
                        String.join(", ", uploadProperties.getAllowTypes()));
            }
        }
    }

    private String getFileExtension(String filename) {
        return FileUtils.getFileExtension(filename);
    }

    public File getDefaultAvatarFile() {
        // 可以配置默认头像路径
        Path defaultPath = Paths.get(uploadProperties.getBaseUrl(), uploadProperties.getAvatarSubDir(), uploadProperties.getDefaultAvatar());
        return defaultPath.toFile();
    }
}