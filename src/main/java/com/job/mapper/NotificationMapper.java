package com.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.job.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
    List<Notification> selectByUserIdPaged(@Param("userId") Long userId, @Param("offset") int offset, @Param("pageSize") int pageSize);
    int countByUserId(@Param("userId") Long userId);
} 