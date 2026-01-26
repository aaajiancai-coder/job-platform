package com.job.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("chat_message")
public class ChatMessage {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String sessionKey;
    private String role; // "user" / "assistant" / "tool"
    private String content;
    private String toolCallId; // For assistant tool calls and tool responses
    private String toolName; 

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
