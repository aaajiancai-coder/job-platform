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
    @Insert("insert into chat_message(session_id, role, content) " +
            "values(#{sessionId}, #{role}, #{content})")
    void save(ChatMessage chatMessage);

    @Select("select * from chat_message where session_id = #{sessionId} order by id asc")
    List<ChatMessage> findBySessionId(Long sessionId);

    @Delete("delete from chat_message where session_id = #{sessionId}")
    void deleteBySessionId(Long sessionId);

    @Select("select * from (select * from chat_message where session_id = #{sessionId} " +
            "order by created_at desc limit #{lastN}) as t order by created_at")
    List<ChatMessage> findBySessionIdAndLimit(Long sessionId, int lastN);
}