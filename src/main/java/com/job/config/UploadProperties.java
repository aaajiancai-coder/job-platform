package com.job.config;

import lombok.Data; // 引入Lombok，自动生成getter/setter
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "upload") // 指定配置前缀
public class UploadProperties {
    // 基础路径，如：D:/job_platform/uploads/
    private String baseUrl;

    // 最大文件大小（字节）
    private Long maxSize;

    // 允许的文件类型
    private List<String> allowTypes;

    // 头像存储子目录
    private String avatarSubDir = "avatars/";

    private String defaultAvatar = "default.jpg";
}