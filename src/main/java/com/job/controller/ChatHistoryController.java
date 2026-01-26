package com.job.controller;

import com.job.vo.MessageVO;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/ai/history")
public class ChatHistoryController {
    @Resource
    private ChatMemory chatMemory;

    @RequestMapping("/{userId}")
    public List<MessageVO> getChatHistory(@PathVariable("userId") String userId) {
        List<Message> messages = chatMemory.get(userId);
        if (messages == null)
        {
            return List.of();
        }
        return messages.stream().map(MessageVO::new).toList();
    }


}
