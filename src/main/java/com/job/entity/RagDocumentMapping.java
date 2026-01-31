package com.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rag_document_mappings")
public class RagDocumentMapping {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long fileId;          // 文件ID（对应rag_files表）

    private String documentId;    // 向量数据库中文档的ID

    private LocalDateTime createdAt;  // 创建时间
}