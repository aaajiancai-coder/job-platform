package com.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.job.entity.Notification;
import com.job.entity.Application;
import com.job.entity.CompanyProfile;
import com.job.entity.StudentProfile;
import com.job.entity.Job;
import com.job.mapper.NotificationMapper;
import com.job.mapper.ApplicationMapper;
import com.job.mapper.CompanyProfileMapper;
import com.job.mapper.StudentProfileMapper;
import com.job.mapper.JobMapper;
import com.job.service.NotificationService;
import com.job.common.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private ApplicationMapper applicationMapper;
    @Autowired
    private CompanyProfileMapper companyProfileMapper;
    @Autowired
    private StudentProfileMapper studentProfileMapper;
    @Autowired
    private JobMapper jobMapper;

    @Override
    public PageResult<Notification> getNotificationList(Long userId, int page, int pageSize) {
        // 参数校验
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (page < 1) {
            page = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = 10;
        }

        // 查询数据
        QueryWrapper<Notification> queryWrapper = new QueryWrapper<Notification>()
                .eq("receiver_id", userId)
                .orderByDesc("created_at");
                
        // 获取总数和分页数据
        Long total = notificationMapper.selectCount(queryWrapper);
        Page<Notification> pageParam = new Page<>(page, pageSize);
        Page<Notification> resultPage = notificationMapper.selectPage(pageParam, queryWrapper);
        
        return new PageResult<>(resultPage.getRecords(), total, page, pageSize);
    }

    @Override
    public void markRead(Long notificationId) {
        if (notificationId == null) {
            throw new IllegalArgumentException("Notification ID cannot be null");
        }
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) return;
        notification.setIsRead(1);
        notificationMapper.updateById(notification);

        // 业务扩展：自动给对方发送已读通知
        // 1. 企业已读学生投递简历的消息，通知学生
        if ("简历投递".equals(notification.getType())) {
            // 查找投递相关信息
            Application app = applicationMapper.selectById(notification.getApplicationId());
            if (app != null) {
                CompanyProfile company = companyProfileMapper.selectByUserId(notification.getReceiverId());
                Job job = jobMapper.selectById(app.getJobId());
                StudentProfile student = studentProfileMapper.selectById(app.getStudentId());
                if (company != null && job != null && student != null) {
                    String content = company.getCompanyName() + "已读了你向" + job.getTitle() + "投递的" + (student.getRealName() != null ? student.getRealName() : "简历") + "。";
                    sendNotification(app.getStudentId(), company.getUserId(), app.getId(), "投递已读", content);
                }
            }
        }
        // 2. 学生已读企业消息（如邀请面试等），通知企业
        if (notification.getSenderId() != null && notification.getType() != null && !"简历投递".equals(notification.getType())) {
            Application app = applicationMapper.selectById(notification.getApplicationId());
            if (app != null) {
                StudentProfile student = studentProfileMapper.selectById(app.getStudentId());
                CompanyProfile company = companyProfileMapper.selectByUserId(notification.getSenderId());
                if (student != null && company != null) {
                    String content = "学生" + (student.getRealName() != null ? student.getRealName() : "") + "已读了你给他发送的" + notification.getType() + "消息。";
                    sendNotification(company.getUserId(), student.getUserId(), app.getId(), "学生已读", content);
                }
            }
        }
    }

    @Override
    public void createNotification(Notification notification) {
        if (notification == null) {
            throw new IllegalArgumentException("Notification cannot be null");
        }
        notificationMapper.insert(notification);
    }

    @Override
    public int getUnreadCount(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        QueryWrapper<Notification> queryWrapper = new QueryWrapper<Notification>()
                .eq("receiver_id", userId)
                .eq("is_read", 0);
                
        Long count = notificationMapper.selectCount(queryWrapper);
        // 安全转换：如果未读消息数超过Integer.MAX_VALUE，返回Integer.MAX_VALUE
        return count > Integer.MAX_VALUE ? Integer.MAX_VALUE : count.intValue();
    }

    @Override
    public void sendNotification(Long receiverId, Long senderId, Long applicationId, String type, String content) {
        // 参数校验
        if (receiverId == null) {
            throw new IllegalArgumentException("Receiver ID cannot be null");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Notification type cannot be null or empty");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Notification content cannot be null or empty");
        }

        // 创建通知对象
        Notification notification = new Notification();
        notification.setReceiverId(receiverId);
        notification.setSenderId(senderId);
        notification.setApplicationId(applicationId);
        notification.setType(type);
        notification.setContent(content);
        notification.setIsRead(0);
        notification.setCreatedAt(new Date());
        notification.setUpdatedAt(new Date());

        // 保存通知
        createNotification(notification);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {
        // 参数校验
        if (notificationId == null) {
            throw new IllegalArgumentException("Notification ID cannot be null");
        }

        // 检查通知是否存在
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            throw new IllegalArgumentException("Notification not found with ID: " + notificationId);
        }

        // 删除通知
        int rows = notificationMapper.deleteById(notificationId);
        if (rows != 1) {
            throw new RuntimeException("Failed to delete notification with ID: " + notificationId);
        }
    }
} 