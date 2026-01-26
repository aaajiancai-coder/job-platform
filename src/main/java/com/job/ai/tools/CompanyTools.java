package com.job.ai.tools;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.job.entity.CompanyProfile;
import com.job.entity.Job;
import com.job.mapper.CompanyProfileMapper;
import com.job.mapper.JobMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CompanyTools {

    private final CompanyProfileMapper companyProfileMapper;

    private final JobMapper jobMapper;

    @Tool(name = "queryCompanyProfile", description = "根据用户提供的条件（如公司名称、行业、规模、地点等）查询匹配的企业信息。" +
            "注意：只有当用户明确提到具体公司名、行业或地点时才调用此工具。" +
            "如果没有任何查询条件，请不要调用此工具。")
    public List<CompanyProfile> queryCompanyProfile(
            @ToolParam(description = "公司名称关键词，例如：腾讯、阿里、字节", required = false) String companyName,
            @ToolParam(description = "所属行业，例如：互联网、金融、电商", required = false) String industry,
            @ToolParam(description = "公司规模，例如：大型、中型、小型", required = false) String companySize,
            @ToolParam(description = "工作地点，例如：北京、上海、深圳", required = false) String location,
            @ToolParam(description = "职位或业务描述关键词", required = false) String description
    ) {
        // 如果所有参数都为空，说明没有有效查询条件
        if (companyName == null && industry == null && companySize == null && location == null && description == null) {
            return Collections.emptyList();
        }

        QueryWrapper<CompanyProfile> queryWrapper = new QueryWrapper<>();

        if (companyName != null) {
            queryWrapper.like("company_name", companyName);
        }
        if (industry != null) {
            queryWrapper.eq("industry", industry);
        }
        if (companySize != null) {
            queryWrapper.eq("company_size", companySize);
        }
        if (location != null) {
            queryWrapper.eq("location", location);
        }
        if (description != null) {
            queryWrapper.like("description", description);
        }

        // 只查询已认证企业
        queryWrapper.eq("verification_status", 1);

        List<CompanyProfile> result = companyProfileMapper.selectList(queryWrapper);
        return result != null ? result : Collections.emptyList();
    }


   @Tool(name = "queryJob", description = "根据条件查询职位：根据职位名称，职位描述，职位需求，职位薪资，职位所在的地市，职位类型，学历要求，经验要求" +
           "注意：只要用户提及到了任意查询条件都可以调用此工具，没有提及的条件不用传参，不能发生用户没有提及就随便传入参数" +
           " 只要有一个条件就可以调用此工具")
    public List<Job> queryJob(
            @ToolParam(description = "职位名称:Java开发工程师,前端开发工程师,产品经理,算法工程师", required = false) String title,
            @ToolParam(description = "职位描述:负责后端开发,负责产品设计,负责前端开发", required = false) String description,
            @ToolParam(description = "职位需求:精通Java,精通Vue,有产品经验,精通算法", required = false) String requirements,
            @ToolParam(description = "职位薪资: 15k-30k, 12k-25k...", required = false) String salaryRange,
            @ToolParam(description = "职位所在地市: 北京，杭州", required = false) String location,
            @ToolParam(description = "职位类型：全职，兼职，实习", required = false) String jobType,
            @ToolParam(description = "学历要求：硕士，本科", required = false) String educationRequirement,
            @ToolParam(description = "经验要求：不限，1年，2年，1年及以上，2年及以上", required = false) String experienceRequirement
    ) {
        QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(title != null, "title", title);
        queryWrapper.like(description != null, "description", description);
        queryWrapper.like(requirements != null, "requirements", requirements);
        queryWrapper.eq(salaryRange != null, "salary_range", salaryRange);
        queryWrapper.eq(location != null, "location", location);
        queryWrapper.eq(jobType != null, "job_type", jobType);
        queryWrapper.eq(educationRequirement != null, "education_requirement", educationRequirement);
        queryWrapper.eq(experienceRequirement != null, "experience_requirement", experienceRequirement);

        List<Job> result = jobMapper.selectList(queryWrapper);
        return result != null ? result : Collections.emptyList();
   }

   @Tool(name = "queryJobByCompanyName", description = "根据公司名称查询该公司的职位信息：当用户说想看哪个企业或公司下有什么职位可以调用此方法")
    public List<Job> queryJobByCompanyName(@ToolParam(description = "公司名称：阿里巴巴，字节跳动，京东") String companyName) {
        QueryWrapper<CompanyProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("company_name", companyName);
        queryWrapper.eq("verification_status", 1);
       List<CompanyProfile> companies = companyProfileMapper.selectList(queryWrapper);
       if (companies.isEmpty()) {
           return Collections.emptyList();
       }
       // 选择第一个通过审核的公司（如果有的话）
       CompanyProfile company = companies.get(0);

       // 查询职位信息
       List<Job> result = jobMapper.selectByCompanyIdPaged(company.getId(), 0, 10);
       return result != null ? result : Collections.emptyList();
    }
}