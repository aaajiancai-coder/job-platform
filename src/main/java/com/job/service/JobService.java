package com.job.service;

import com.job.common.page.PageResult;
import com.job.dto.JobWithCompanyDTO;
import com.job.entity.Job;
 
public interface JobService {
    PageResult<JobWithCompanyDTO> getJobsByParams(String keyword, String location, String type, int page, int pageSize);
    PageResult<Job> getJobsByEntity(Job job, int page, int pageSize);
    PageResult<JobWithCompanyDTO> getJobsWithCompany(Job job, int page, int pageSize);
    JobWithCompanyDTO getJobDetail(Long id);
    // 企业端：分页获取本公司职位列表
    PageResult<Job> getJobsByCompany(Long companyId, int page, int pageSize);
    // 企业端：新增职位
    boolean createJob(Job job);
    // 企业端：编辑职位
    boolean updateJob(Job job);
    // 企业端：删除职位
    boolean deleteJob(Long id);
    // 企业端：上下架职位
    boolean changeJobStatus(Long id, Integer status);
}