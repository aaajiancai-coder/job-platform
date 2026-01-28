package com.job.entity;


import lombok.Data;

/**
 * 记录chatId会话历史的实体类
 * @author GM
 */
@Data
public class ChatHistory {
    private String id;
    private String type;
    private String chatId;
}