package com.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.job.entity.ChatMessage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    @Insert("insert into chat_message(session_key, role, content, tool_call_id, tool_name) " +
            "values(#{sessionKey}, #{role}, #{content}, #{toolCallId}, #{toolName})")
    void save(ChatMessage chatMessage);

    @Select("select * from chat_message where session_key = #{sessionKey} order by id asc")
    List<ChatMessage> findBySessionId(String sessionKey);

    @Delete("delete from chat_message where session_key = #{sessionKey}")
    void deleteBySessionId(String sessionKey);

    @Select("select * from (select * from chat_message where session_key = #{sessionKey} " +
            "order by created_at desc limit #{lastN}) as t order by created_at")
    List<ChatMessage> findBySessionIdAndLimit(String sessionKey, int lastN);
}