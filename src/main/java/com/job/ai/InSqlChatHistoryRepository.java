package com.job.ai;

import com.job.entity.ChatSession;
import com.job.mapper.ChatSessionMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class InSqlChatHistoryRepository implements ChatHistoryRepository{
    @Resource
    private ChatSessionMapper chatSessionMapper;


    @Override
    public void save(String userId) {
        if(null != chatSessionMapper.selectByUserId(userId)) {
            return;
        }
        ChatSession chatSession = new ChatSession();
        try {
            chatSession.setUserId(Long.valueOf(userId));
        } catch (NumberFormatException e) {
            // Default ID for guest or non-numeric userId
            chatSession.setUserId(0L);
        }
        chatSession.setSessionKey(userId);
        chatSessionMapper.insert(chatSession);
    }

    @Override
    public void delete(String userId) {

    }
}
