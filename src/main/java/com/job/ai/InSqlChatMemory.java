package com.job.ai;

import com.job.entity.ChatMessage;
import com.job.mapper.ChatMessageMapper;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class InSqlChatMemory implements ChatMemory {
    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Override
    public void add(String conversationId, List<Message> messages) {
        Assert.hasText(conversationId, "conversationId must not be empty");
        Assert.notEmpty(messages, "messages must not be empty");
        Assert.noNullElements(messages, "messages cannot contain null elements");// 确保messages中不包含null元素

        for(Message message : messages) {
            String role = "";
            switch (message.getMessageType()) {
                case USER:
                    role = "user";
                    break;
                case ASSISTANT:
                    role = "assistant";
                    break;
                default:
                    role = "unknown";
                    break;
            }
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setConversationId(conversationId);
            chatMessage.setRole(role);
            chatMessage.setContent(message.getText());
            chatMessageMapper.save(chatMessage);
        }
    }

    @Override
    public List<Message> get(String conversationId) {
        Assert.hasText(conversationId, "conversationId must not be empty");
        List<ChatMessage> chatMessages = chatMessageMapper.findByConversationId(conversationId);

        List<Message> messages = new ArrayList<>();
        for(ChatMessage chatMessage : chatMessages) {
            switch (chatMessage.getRole()) {
                case "user":
                    messages.add(new UserMessage(chatMessage.getContent()));
                    break;
                case "assistant":
                    messages.add(new AssistantMessage(chatMessage.getContent()));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown role: " + chatMessage.getRole());
            }
        }
        return messages;
    }

    @Override
    public void clear(String conversationId) {
        chatMessageMapper.deleteByConversationId(conversationId);
    }
}
