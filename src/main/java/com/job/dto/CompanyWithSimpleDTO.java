package com.job.dto;

import lombok.Data;

@Data
public class CompanyWithSimpleDTO {
    private Long id;
    private String companyName;
    private String location;
    private String industry;
    private String companySize;
} 