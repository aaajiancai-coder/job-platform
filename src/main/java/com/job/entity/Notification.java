package com.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("notifications")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long receiverId;
    private Long senderId;
    private Long applicationId;
    private String type;
    private String content;
    private Integer isRead;
    private Date createdAt;
    private Date updatedAt;
} 