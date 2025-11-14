package com.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("company_profiles")
public class CompanyProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String companyName;
    private String industry;
    private String companySize;
    private String location;
    private String description;
    private String logoUrl;
    private String website;
    private Integer verificationStatus;
    
    // 审核状态：0-待审核 1-审核通过 2-审核拒绝
    private String auditStatus;
    // 审核原因
    private String auditReason;
    // 审核时间
    private LocalDateTime auditTime;
} 