package com.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("jobs")
public class Job {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long companyId;
    private String title;
    private String description;
    private String requirements;
    private String salaryRange;
    private String location;
    private String jobType;
    private String educationRequirement;
    private String experienceRequirement;
    private Integer status;
    private Date createdAt;
    private Date updatedAt;
} 