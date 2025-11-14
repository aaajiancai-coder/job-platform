package com.job.dto;

import lombok.Data;

@Data
public class CompanyDashboardDTO {
    private String companyName;
    private String logoUrl;
    private String industry;
    private Integer verificationStatus;
    private Integer jobCount;
    private Integer resumeCount;
    private Integer interviewCount;
}
