package com.job.service.impl;

import com.job.dto.JobSimpleDTO;
import com.job.dto.CompanySimpleDTO;
import com.job.mapper.JobMapper;
import com.job.mapper.CompanyProfileMapper;
import com.job.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommonServiceImpl implements CommonService {
    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private CompanyProfileMapper companyProfileMapper;

    @Override
    public List<JobSimpleDTO> getRandomJobs(int count) {
        return jobMapper.selectRandomSimpleJobs(count);
    }

    @Override
    public List<CompanySimpleDTO> getRandomCompanies(int count) {
        return companyProfileMapper.selectRandomSimpleCompanies(count);
    }
}
