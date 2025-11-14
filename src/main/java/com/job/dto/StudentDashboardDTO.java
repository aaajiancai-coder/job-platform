package com.job.dto;

import com.job.entity.StudentProfile;
import lombok.Data;

@Data
public class StudentDashboardDTO {
    private String name;
    private String school;
    private String major;
    private String avatar;
    private int applied;
    private int interview;
    private int offer;
    private StudentProfile studentProfile;
} 