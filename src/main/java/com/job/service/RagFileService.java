package com.job.service;

import com.job.entity.RagFile;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RagFileService extends IService<RagFile> {
    
    /**
     * 上传文件并将其向量化存储
     */
    boolean uploadAndEmbedFile(MultipartFile file, Long userId) throws Exception;
    
    /**
     * 删除文件及其向量数据
     */
    boolean deleteFile(Long fileId, Long userId) throws Exception;
    
    /**
     * 获取用户上传的所有文件列表
     */
    List<RagFile> getUserFiles(Long userId);
    
    /**
     * 检查文件类型是否支持
     */
    boolean isSupportedFileType(String fileName);
}