package com.job.utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileUtils {
    
    // 支持的文件类型
    public static final List<String> SUPPORTED_FILE_TYPES = Arrays.asList(
            ".txt", ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".html", ".htm", ".xml", ".json"
    );
    
    /**
     * 检查文件类型是否支持
     */
    public static boolean isSupportedFileType(String fileName) {
        if (fileName == null) {
            return false;
        }
        String extension = getFileExtension(fileName).toLowerCase();
        return SUPPORTED_FILE_TYPES.contains(extension);
    }
    
    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
    
    /**
     * 创建目录
     */
    public static boolean createDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return true;
    }
}