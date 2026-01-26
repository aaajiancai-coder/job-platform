package com.job.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("chat_session")
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String sessionKey;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}