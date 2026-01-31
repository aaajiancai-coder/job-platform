package com.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.job.entity.RagDocumentMapping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RagDocumentMappingMapper extends BaseMapper<RagDocumentMapping> {
    
    /**
     * 根据文件ID获取所有文档ID
     */
    List<RagDocumentMapping> findByFileId(@Param("fileId") Long fileId);
    
    /**
     * 根据文件ID删除所有映射记录
     */
    int deleteByFileId(@Param("fileId") Long fileId);
}