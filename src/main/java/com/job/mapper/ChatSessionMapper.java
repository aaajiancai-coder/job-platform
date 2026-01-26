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
    @Insert("insert into chat_session(id, user_id, session_key) value (#{userId}, #{userId}, #{userId})")
    int insert(ChatSession chatSession);

    @Delete("delete from chat_session where user_id = #{userId}")
    int delete(String userId);

    @Select("select * from chat_session where user_id = #{userId}")
    ChatSession selectByUserId(String userId);
}
