package com.job.ai.query;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

@Data
public class CompanyQuery {
    @ToolParam(required = false, description = "公司名称")
    private String companyName;
    @ToolParam(required = false, description = "行业")
    private String industry;
    @ToolParam(required = false, description = "公司规模: 1000+, 100+, 1000-5000, 5000-10000")
    private String companySize;
    @ToolParam(required = false, description = "公司所在地市: 北京， 杭州， 浙江")
    private String location;
    @ToolParam(required = false, description = "公司简介")
    private String description;
}
