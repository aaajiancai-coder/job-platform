package com.job.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ApplicationWithJobCompanyDTO {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private Long resumeId;
    private String status;
    private Date applyTime;
    private Date updatedAt;
    private String studentName;
} 