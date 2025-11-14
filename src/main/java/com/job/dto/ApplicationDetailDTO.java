package com.job.dto;

import com.job.entity.Application;
import com.job.entity.Resume;
import com.job.entity.StudentProfile;
import lombok.Data;

@Data
public class ApplicationDetailDTO {
    private Application application;
    private StudentProfile student;
    private Resume resume;
} 