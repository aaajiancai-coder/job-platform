package com.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.job.common.page.PageResult;
import com.job.entity.Job;
import com.job.dto.JobWithCompanyDTO;
import com.job.mapper.JobMapper;
import com.job.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class JobServiceImpl implements JobService {
    @Autowired
    private JobMapper jobMapper;

    @Override
    public PageResult<JobWithCompanyDTO> getJobsByParams(String keyword, String location, String type, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<JobWithCompanyDTO> records = jobMapper.selectJobsWithCompanyKeyword(keyword, location, type, offset, pageSize);
        int total = jobMapper.countJobsWithCompanyKeyword(keyword, location, type);
        return new PageResult<>(records, total, page, pageSize);
    }

    @Override
        public PageResult<Job> getJobsByEntity(Job job, int page, int pageSize) {
        QueryWrapper<Job> wrapper = new QueryWrapper<>(job);
        wrapper.eq("status", 1);
        wrapper.orderByDesc("created_at");
        Page<Job> pageObj = new Page<>(page, pageSize);
        jobMapper.selectPage(pageObj, wrapper);
        return new PageResult<>(pageObj.getRecords(), pageObj.getTotal(), page, pageSize);
    }

    @Override
    public PageResult<JobWithCompanyDTO> getJobsWithCompany(Job job, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<JobWithCompanyDTO> records = jobMapper.selectJobsWithCompanyName(job, offset, pageSize);
        int total = jobMapper.countJobsWithCompanyName(job);
        return new PageResult<>(records, total, page, pageSize);
    }

    @Override
    public JobWithCompanyDTO getJobDetail(Long id) {
        return jobMapper.selectJobWithCompanyById(id);
    }
   //废弃
    @Override
    public PageResult<Job> getJobsByCompany(Long companyId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Job> records = jobMapper.selectByCompanyIdPaged(companyId, offset, pageSize);
        int total = jobMapper.countByCompanyId(companyId);
        return new PageResult<>(records, total, page, pageSize);
    }

    @Override
    public boolean createJob(Job job) {

        return jobMapper.insert(job) > 0;
    }

    @Override
    public boolean updateJob(Job job) {
        return jobMapper.updateById(job) > 0;
    }

    @Override
    public boolean deleteJob(Long id) {
        return jobMapper.deleteById(id) > 0;
    }

    @Override
    public boolean changeJobStatus(Long id, Integer status) {
        Job job = new Job();
        job.setId(id);
        job.setStatus(status);
        return jobMapper.updateById(job) > 0;
    }
} 