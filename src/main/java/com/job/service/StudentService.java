package com.job.service;

import com.job.dto.StudentDashboardDTO;

public interface StudentService {
    StudentDashboardDTO getDashboardInfo(Long studentId);
    com.job.entity.StudentProfile getByUserId(Long userId);
    com.job.entity.StudentProfile getStudentDetail(Long id);
    boolean updateStudentDetail(com.job.entity.StudentProfile profile);
} 