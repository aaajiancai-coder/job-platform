package com.job.ai;
 
import com.job.entity.ChatMessage;
import com.job.mapper.ChatMessageMapper;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class InSqlChatMemory implements ChatMemory {
    @Resource
    private ChatMessageMapper chatMessageMapper;

    @Override
    public void add(String conversationId, List<Message> messages) {
        for (Message message : messages) {
            switch (message.getMessageType()) {
                case USER:
                    saveMessage(conversationId, "user", message.getText(), null, null);
                    break;
                case ASSISTANT:
                    AssistantMessage assistantMessage = (AssistantMessage) message;
                    if (assistantMessage.hasToolCalls()) {
                        for (AssistantMessage.ToolCall toolCall : assistantMessage.getToolCalls()) {
                            saveMessage(conversationId, "assistant", toolCall.arguments(), 
                                toolCall.id(), toolCall.name());
                        }
                    } else {
                        saveMessage(conversationId, "assistant", assistantMessage.getText(), null, null);
                    }
                    break;
                case TOOL:
                    ToolResponseMessage toolMessage = (ToolResponseMessage) message;
                    for (ToolResponseMessage.ToolResponse response : toolMessage.getResponses()) {
                        saveMessage(conversationId, "tool", response.responseData().toString(), 
                            response.id(), response.name());
                    }
                    break;
            }
        }
    }

    private void saveMessage(String sessionKey, String role, String content, String toolCallId, String toolName) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSessionKey(sessionKey);
        chatMessage.setRole(role);
        chatMessage.setContent(content);
        chatMessage.setToolCallId(toolCallId);
        chatMessage.setToolName(toolName);
        chatMessageMapper.save(chatMessage);
    }

    @Override
    public List<Message> get(String conversationId) {
        Assert.hasText(conversationId, "conversationId cannot be null or empty");
        List<ChatMessage> chatMessages = chatMessageMapper.findBySessionId(conversationId);
        List<Message> messages = new ArrayList<>();
        
        // Group by role and toolCallId to reconstruct AssistantMessage with multiple tool calls if needed
        // but for simplicity, we'll handle them one by one or reconstruct carefully.
        
        for (ChatMessage cm : chatMessages) {
            switch (cm.getRole()) {
                case "user":
                    messages.add(new UserMessage(cm.getContent()));
                    break;
                case "assistant":
                    if (cm.getToolCallId() != null) {
                        AssistantMessage.ToolCall toolCall = new AssistantMessage.ToolCall(
                            cm.getToolCallId(), "function", cm.getToolName(), cm.getContent());
                        messages.add(new AssistantMessage(cm.getContent(), Map.of(), List.of(toolCall)));
                    } else {
                        messages.add(new AssistantMessage(cm.getContent()));
                    }
                    break;
                case "tool":
                    ToolResponseMessage.ToolResponse response = new ToolResponseMessage.ToolResponse(
                        cm.getToolCallId(), cm.getToolName(), cm.getContent());
                    messages.add(new ToolResponseMessage(List.of(response), Map.of()));
                    break;
            }
        }
        return messages;
    }



    @Override
    public void clear(String conversationId) {
        chatMessageMapper.deleteBySessionId(conversationId);
    }
}
