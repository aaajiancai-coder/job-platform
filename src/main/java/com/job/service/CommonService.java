package com.job.service;

import com.job.dto.JobSimpleDTO;
import com.job.dto.CompanySimpleDTO;
import java.util.List;

public interface CommonService {
    List<JobSimpleDTO> getRandomJobs(int count);
    List<CompanySimpleDTO> getRandomCompanies(int count);
}
