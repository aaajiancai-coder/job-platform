package com.job.config;

import com.job.ai.InSqlChatMemory;
import com.job.ai.tools.CompanyTools;
import com.job.constants.SystemConstants;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class CommonConfiguration {
    @Bean
    public VectorStore vectorStore(EmbeddingModel model) {
        return SimpleVectorStore.builder(model).build();
    }
    @Bean
    public ChatClient jobPlatformChatClient(ChatModel model, ChatMemory chatMemory, CompanyTools companyTools) {
        return ChatClient
                .builder(model)
                .defaultSystem(SystemConstants.JOB_PLATFORM_SYSTEM_PROMPT)
                .defaultAdvisors(new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultTools(companyTools)
                .build();
    }

    @Bean
    public ChatMemory chatMemory() {
        return new InSqlChatMemory();
    }
}