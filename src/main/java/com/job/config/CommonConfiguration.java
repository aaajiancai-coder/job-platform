package com.job.config;


import com.job.ai.CompanyTools;
import com.job.ai.InSqlChatMemory;
import com.job.constants.SystemConstants;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.Arrays;

@Configuration
public class CommonConfiguration {
    @Bean
    public ChatClient jobPlatformChatClient(ChatClient.Builder builder, ChatMemory chatMemory, CompanyTools companyTools) {
        return builder
                .defaultSystem(SystemConstants.JOB_PLATFORM_SYSTEM_PROMPT)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultTools(companyTools)
                .build();
    }

    @Bean
    public ChatClient vectorChatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        return builder
                .defaultSystem(SystemConstants.VECTOR_SYSTEM_PROMPT)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultAdvisors()
                .build();
    }

    @Bean
    public ChatMemory chatMemory() {
        return new  InSqlChatMemory();
    }


}