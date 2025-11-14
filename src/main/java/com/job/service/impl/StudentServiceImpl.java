package com.job.service.impl;

import com.job.dto.StudentDashboardDTO;
import com.job.entity.StudentProfile;
import com.job.entity.User;
import com.job.mapper.ApplicationMapper;
import com.job.mapper.StudentProfileMapper;
import com.job.mapper.UserMapper;
import com.job.service.NotificationService;
import com.job.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentProfileMapper studentProfileMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ApplicationMapper applicationMapper;
    @Autowired
    private NotificationService notificationService;
    

    @Override
    public StudentDashboardDTO getDashboardInfo(Long studentId) {
        StudentProfile profile = studentProfileMapper.selectById(studentId);
        if (profile == null) {
            throw new RuntimeException("未找到该学生的详细信息，请先完善个人资料！");
        }
        User user = userMapper.selectById(profile.getUserId());

        int applied = applicationMapper.countByStudentId(studentId);
        int interview = notificationService.getUnreadCount(profile.getUserId());
        int offer = applicationMapper.countByStudentIdAndStatus(studentId, "已发送offer");

        StudentDashboardDTO dto = new StudentDashboardDTO();
        dto.setName(profile.getRealName());
        dto.setSchool(profile.getUniversity());
        dto.setMajor(profile.getMajor());
        dto.setAvatar(user.getAvatar());
        dto.setApplied(applied);
        dto.setInterview(interview);
        dto.setOffer(offer);
        dto.setStudentProfile(profile);
        return dto;
    }

    @Override
    public StudentProfile getByUserId(Long userId) {
        return studentProfileMapper.selectByUserId(userId);
    }

    @Override
    public com.job.entity.StudentProfile getStudentDetail(Long id) {
        return studentProfileMapper.selectById(id);
    }

    @Override
    public boolean updateStudentDetail(com.job.entity.StudentProfile profile) {
        int result = studentProfileMapper.updateById(profile);
        return result > 0;
    }
} 