package com.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.job.entity.Application;
import com.job.dto.ApplicationWithJobCompanyDTO;
import com.job.dto.ApplicationDetailDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {
    List<Application> selectByStudentId(@Param("studentId") Long studentId);
    void withdrawById(@Param("applicationId") Long applicationId);
    
    int countByStudentId(@Param("studentId") Long studentId);

    int countByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") String status);

    List<ApplicationWithJobCompanyDTO> selectWithJobAndCompanyByStudentId(@Param("studentId") Long studentId);

    List<ApplicationWithJobCompanyDTO> selectWithJobAndCompanyByStudentIdPaged(@Param("studentId") Long studentId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    int countByJobIds(@Param("jobIds") List<Long> jobIds);

    int countByJobIdsAndStatus(@Param("jobIds") List<Long> jobIds, @Param("status") String status);

    List<ApplicationWithJobCompanyDTO> selectCompanyResumes(@Param("companyId") Long companyId, @Param("jobId") Long jobId, @Param("status") String status, @Param("offset") int offset, @Param("pageSize") int pageSize);

    int countCompanyResumes(@Param("companyId") Long companyId, @Param("jobId") Long jobId, @Param("status") String status);

    void updateResumeStatus(@Param("applicationId") Long applicationId, @Param("status") String status);

    ApplicationDetailDTO selectApplicationDetail(@Param("applicationId") Long applicationId);

    // 统计某天职位投递数
    int countByDate(@Param("date") String date);
}   