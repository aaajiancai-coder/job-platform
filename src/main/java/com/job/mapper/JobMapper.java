package com.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.job.entity.Job;
import com.job.dto.JobSimpleDTO;
import com.job.dto.JobWithCompanyDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface JobMapper extends BaseMapper<Job> {
    @Select("SELECT id, title, location, salary_range AS salaryRange FROM jobs ORDER BY RAND() LIMIT #{count}")
    List<JobSimpleDTO> selectRandomSimpleJobs(int count);

    List<JobWithCompanyDTO> selectJobsWithCompanyName(
        @Param("job") Job job,
        @Param("offset") int offset,
        @Param("pageSize") int pageSize
    );
    int countJobsWithCompanyName(@Param("job") Job job);

    List<JobWithCompanyDTO> selectJobsWithCompanyKeyword(
        @Param("keyword") String keyword,
        @Param("location") String location,
        @Param("type") String type,
        @Param("offset") int offset,
        @Param("pageSize") int pageSize
    );
    int countJobsWithCompanyKeyword(
        @Param("keyword") String keyword,
        @Param("location") String location,
        @Param("type") String type
    );

    JobWithCompanyDTO selectJobWithCompanyById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM jobs WHERE company_id = #{companyId}")
    int countByCompanyId(@Param("companyId") Long companyId);

    // 企业端：分页获取本公司职位列表
    List<Job> selectByCompanyIdPaged(@Param("companyId") Long companyId, @Param("offset") int offset, @Param("pageSize") int pageSize);
}
