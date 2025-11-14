package com.job.dto;

import lombok.Data;

@Data
public class JobWithCompanyDTO {
    private Long id;
    private String title;
    private String companyName;
    private String salaryRange;
    private String location;
    private String jobType;
    private String educationRequirement;
    private String experienceRequirement;
    private String description;
    private String requirements;
} 