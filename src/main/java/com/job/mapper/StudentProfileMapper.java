package com.job.mapper;

import com.job.entity.StudentProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface StudentProfileMapper extends BaseMapper<StudentProfile> {
    StudentProfile selectByUserId(@Param("userId") Long userId);
} 