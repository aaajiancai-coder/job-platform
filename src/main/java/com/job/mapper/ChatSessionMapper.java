package com.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.job.entity.ChatSession;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

@Mapper
public interface ChatSessionMapper extends BaseMapper<ChatSession> {

    @Delete("delete from chat_session where user_id = #{userId}")
    int deleteByUserId(String userId);

    @Select("select * from chat_session where session_key = #{sessionKey}")
    ChatSession selectBySessionKey(String sessionKey);
}
