package com.job.dto;

import lombok.Data;

@Data
public class JobSimpleDTO {
    private Long id;
    private String title;
    private String location;
    private String salaryRange;
}
