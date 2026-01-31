package com.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rag_files")
public class RagFile {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String filename;           // 原始文件名

    private String storedFilename;     // 存储的文件名

    private String filePath;          // 文件存储路径

    private Long fileSize;            // 文件大小（字节）

    private String fileType;          // 文件类型（扩展名）

    private Long uploadUserId;        // 上传用户ID

    private Integer chunkCount;       // 文档切片数量

    private Integer status;           // 状态：1-正常，0-已删除

    private LocalDateTime createdAt;  // 创建时间

    private LocalDateTime updatedAt;  // 更新时间
}