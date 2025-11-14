package com.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("resumes")
public class Resume {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private String title;
    private String content;
    private Integer templateId;
    private Integer status;
    private Date createdAt;
    private Date updatedAt;
} 