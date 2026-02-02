package com.job.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.*;
import java.util.UUID;

/**
 * 头像文件管理工具类
 */
public class AvatarFileUtil {

    // 头像存储根目录
    private static final String AVATAR_BASE_DIR = "/uploads/avatars/";
    // 允许的文件类型
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif"};
    // 最大文件大小：2MB
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;

    /**
     * 上传头像文件
     */
    public static String uploadAvatar(MultipartFile file, HttpServletRequest request) throws IOException {
        // 验证文件
        validateFile(file);

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID() + fileExtension;

        // 创建存储目录
        String uploadDir = getUploadRealPath(request);
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 保存文件
        Path filePath = uploadPath.resolve(newFilename);
        file.transferTo(filePath.toFile());

        // 返回相对路径
        return AVATAR_BASE_DIR + newFilename;
    }

    /**
     * 删除头像文件
     */
    public static boolean deleteAvatar(String avatarPath, HttpServletRequest request) {
        if (avatarPath == null || avatarPath.trim().isEmpty()) {
            return true;
        }

        try {
            String realPath = getUploadRealPath(request);
            String filename = extractFilename(avatarPath);
            Path filePath = Paths.get(realPath, filename);

            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("删除头像文件失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取头像文件
     */
    public static File getAvatarFile(String avatarPath, HttpServletRequest request) {
        if (avatarPath == null || avatarPath.trim().isEmpty()) {
            return null;
        }

        String realPath = getUploadRealPath(request);
        String filename = extractFilename(avatarPath);
        Path filePath = Paths.get(realPath, filename);

        return filePath.toFile();
    }

    /**
     * 验证文件合法性
     */
    private static void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过2MB");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename).toLowerCase();

        boolean allowed = false;
        for (String ext : ALLOWED_EXTENSIONS) {
            if (ext.equalsIgnoreCase(fileExtension)) {
                allowed = true;
                break;
            }
        }

        if (!allowed) {
            throw new IllegalArgumentException("只支持JPG、JPEG、PNG、GIF格式的图片");
        }
    }

    private static String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    private static String getUploadRealPath(HttpServletRequest request) {
        return request.getServletContext().getRealPath(AVATAR_BASE_DIR);
    }

    private static String extractFilename(String avatarPath) {
        return avatarPath.substring(avatarPath.lastIndexOf("/") + 1);
    }

    /**
     * 生成缩略图路径
     */
    public static String generateThumbnailPath(String avatarPath) {
        if (avatarPath == null) return null;
        int dotIndex = avatarPath.lastIndexOf(".");
        if (dotIndex == -1) return avatarPath + "_thumb";
        return avatarPath.substring(0, dotIndex) + "_thumb" + avatarPath.substring(dotIndex);
    }
}
