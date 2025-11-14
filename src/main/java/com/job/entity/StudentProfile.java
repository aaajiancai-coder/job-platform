package com.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("student_profiles")
public class StudentProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String realName;
    private String gender;
    private Date birthDate;
    private String university;
    private String major;
    private String education;
    private Integer graduationYear;
    private String skills;
    private String introduction;
} 