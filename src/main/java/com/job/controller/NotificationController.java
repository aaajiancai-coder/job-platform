package com.job.controller;

import com.job.common.api.ApiResult;
import com.job.common.page.PageResult;
import com.job.entity.Notification;
import com.job.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    // 分页获取通知列表
    @GetMapping("/list")
    public ApiResult<PageResult<Notification>> getNotificationList(
        @RequestParam Long userId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        return ApiResult.success(notificationService.getNotificationList(userId, page, pageSize));
    }

    // 标记已读
    @PostMapping("/read/{notificationId}")
    public ApiResult<Void> markNotificationRead(@PathVariable Long notificationId) {
        notificationService.markRead(notificationId);
        return ApiResult.success(null);
    }

    // 删除通知
    @DeleteMapping("/{notificationId}")
    public ApiResult<Void> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ApiResult.success(null);
    }

    // 获取未读消息数
    @GetMapping("/unread-count")
    public ApiResult<Integer> getUnreadCount(@RequestParam Long userId) {
        int count = notificationService.getUnreadCount(userId);
        return ApiResult.success(count);
    }
}
