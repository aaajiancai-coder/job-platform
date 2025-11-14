package com.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.job.entity.CompanyProfile;
import com.job.dto.CompanySimpleDTO;
import com.job.dto.CompanyWithSimpleDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CompanyProfileMapper extends BaseMapper<CompanyProfile> {
    @Select("SELECT id, company_name AS companyName, location, company_size AS companySize FROM company_profiles WHERE verification_status=1 ORDER BY RAND() LIMIT #{count}")
    List<CompanySimpleDTO> selectRandomSimpleCompanies(int count);

    List<CompanyWithSimpleDTO> selectCompaniesWithSimple(
        @Param("company") CompanyProfile company,
        @Param("offset") int offset,
        @Param("pageSize") int pageSize
    );
    int countCompaniesWithSimple(@Param("company") CompanyProfile company);

    // 自定义SQL查询，企业审核分页用
    List<CompanyProfile> selectListRaw(@org.apache.ibatis.annotations.Param("sql") String sql);
    Integer countRaw(@org.apache.ibatis.annotations.Param("sql") String sql);

    CompanyProfile selectByUserId(@Param("userId") Long userId);
}
