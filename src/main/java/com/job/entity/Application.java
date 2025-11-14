package com.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("applications")
public class Application {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long jobId;
    private Long studentId;
    private Long resumeId;
    private String status;
    private Date applyTime;
    private Date updatedAt;
} 