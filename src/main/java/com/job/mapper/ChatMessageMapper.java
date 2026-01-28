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
    @Insert("insert into chat_message(conversation_id, role, content) " +
            "values(#{conversationId}, #{role}, #{content})")
    void save(ChatMessage chatMessage);

    @Select("select * from chat_message where conversation_id = #{conversationId} order by id asc")
    List<ChatMessage> findByConversationId(String conversationId);

    @Delete("delete from chat_message where conversation_id = #{conversationId}")
    void deleteByConversationId(String conversationId);

}