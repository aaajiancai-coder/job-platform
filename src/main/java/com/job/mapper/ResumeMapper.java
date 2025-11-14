package com.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.job.entity.Resume;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ResumeMapper extends BaseMapper<Resume> {
    List<Resume> selectByStudentId(@Param("studentId") Long studentId);
    List<Resume> selectByStudentIdPaged(@Param("studentId") Long studentId, @Param("offset") int offset, @Param("pageSize") int pageSize);
    int countByStudentId(@Param("studentId") Long studentId);
    int countByDate(@Param("date") String date);
} 