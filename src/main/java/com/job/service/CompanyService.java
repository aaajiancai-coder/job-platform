package com.job.service;

import com.job.common.page.PageResult;
import com.job.dto.CompanyWithSimpleDTO;
import com.job.entity.CompanyProfile;
 
public interface CompanyService {
    PageResult<CompanyWithSimpleDTO> getCompaniesWithSimple(CompanyProfile company, int page, int pageSize);
    CompanyProfile getById(Long id);
    java.util.Map<String, Object> getDashboardInfoWithNotification(Long companyId);
    boolean updateCompanyDetail(CompanyProfile profile);
} 