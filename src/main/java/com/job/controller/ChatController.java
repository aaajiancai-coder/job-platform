package com.job.controller;

import com.job.ai.ChatHistoryRepository;
import com.job.ai.ChatType;
import com.job.ai.CompanyTools;
import lombok.SneakyThrows;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/ai")
public class ChatController {

    @Autowired
    private VectorStore vectorStore;
    @Autowired
    @Qualifier("jobPlatformChatClient")
    private ChatClient jobPlatformChatClient;
    @Autowired
    @Qualifier("vectorChatClient")
    private ChatClient vectorChatClient;
    @Autowired
    private ChatHistoryRepository inSqlChatHistoryRepository;


    @RequestMapping(value = "/smart", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chat(@RequestParam("prompt") String prompt,
                             @RequestParam("userId") String userId) {
        inSqlChatHistoryRepository.save(ChatType.CHAT.getValue(), userId);
        return jobPlatformChatClient.prompt()
                .user(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId))
                .stream()
                .content()
                .map(content -> ServerSentEvent.builder(content).build());
    }




    /**
     * 从向量数据库中查找文档，并将查询的文档作为上下文回答。
     *
     * @param prompt 用户的提问
     * @return SSE流响应
     */
    @GetMapping(value = "database", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatStreamWithDatabase(@RequestParam String prompt,
                                               @RequestParam String userId) {
        if ("guest".equals(userId)) {
            return Flux.just(ServerSentEvent.builder("抱歉，游客身份暂无知识检索权限，请登录后再试哦！").build());
        }
        // question_answer_context是一个占位符，会替换成向量数据库中查询到的文档。QuestionAnswerAdvisor会替换。
        String promptWithContext = """
                {query}
                下面是上下文信息
                ---------------------
                {question_answer_context}
                ---------------------
                严格基于给定的上下文，而不是事先的知识，回复用户的意见，必须原样输出与用户问题匹配的上下文信息。如果答案不在上下文中，告诉用户你不能回答这个问题。
                """;
        inSqlChatHistoryRepository.save(ChatType.CHAT.getValue(), userId);
        return vectorChatClient.prompt()
                .user(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId))
                .advisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder().similarityThreshold(0.8d).topK(1).build())
                        .promptTemplate(new PromptTemplate(promptWithContext)).build())
                .stream()
                .content()
                .map(content -> ServerSentEvent.builder(content).build());
    }
}
