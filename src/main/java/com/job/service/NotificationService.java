package com.job.service;

import com.job.entity.Notification;
import com.job.common.page.PageResult;

public interface NotificationService {
    /**
     * 获取用户的通知列表
     */
    PageResult<Notification> getNotificationList(Long userId, int page, int pageSize);
    
    /**
     * 标记通知为已读
     */
    void markRead(Long notificationId);
    
    /**
     * 创建新通知
     */
    void createNotification(Notification notification);
    
    /**
     * 获取未读通知数量
     */
    int getUnreadCount(Long userId);

    /**
     * 发送通知
     * @param receiverId 接收者ID
     * @param senderId 发送者ID
     * @param applicationId 申请ID
     * @param type 通知类型
     * @param content 通知内容
     */
    void sendNotification(Long receiverId, Long senderId, Long applicationId, String type, String content);

    /**
     * 删除通知
     * @param notificationId 通知ID
     */
    void deleteNotification(Long notificationId);
} 