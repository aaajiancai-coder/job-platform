package com.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.job.entity.ChatHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatHistoryMapper extends BaseMapper<ChatHistory> {

    /**
     * 插入一条聊天记录
     *
     * @param chatHistory
     * @return
     */
    @Insert("INSERT INTO chat_history (type, chat_id) VALUES (#{type}, #{chatId})")
    int insert(ChatHistory chatHistory);
    /**
     * 删除一条聊天记录
     * @param type
     * @param chatId
     */
    @Delete("DELETE FROM chat_history WHERE type = #{type} AND chat_id = #{chatId}")
    void delete(@Param("type") String type, @Param("chatId") String chatId);
    /**
     * 根据type获取聊天记录的chatIds
     * @param type
     * @return
     */
    @Select("SELECT chat_id FROM chat_history WHERE type = #{type}")
    List<String> selectChatIdsByType(String type);
}
