package com.job.dto;

import lombok.Data;

@Data
public class CompanySimpleDTO {
    private Long id;
    private String companyName;
    private String location;
    private String companySize;
}
