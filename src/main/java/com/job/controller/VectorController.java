package com.job.controller;

import com.job.common.api.ApiResult;
import lombok.SneakyThrows;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/vector")
public class VectorController {

    @Autowired
    private VectorStore vectorStore;

    /**
     * 嵌入文件
     *
     * @param file 待嵌入的文件
     * @return 是否成功
     */
    @SneakyThrows
    @PostMapping("embedding")
    public ApiResult embedding(@RequestParam MultipartFile file) {
        // 从IO流中读取文件
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(new InputStreamResource(file.getInputStream()));
        // 将文本内容划分成更小的块
        // 配置更小的切片参数
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(200)        // 每个分块的token数量目标值（默认800）
                .withMinChunkSizeChars(50) // 每个分块的最小字符数（默认350）
                .withMaxNumChunks(50)      // 最大分块数量限制
                .withMinChunkLengthToEmbed(10) // 要包含的分块最小长度（默认5）
                .build();
        List<Document> splitDocuments = splitter.apply(tikaDocumentReader.read());
        vectorStore.add(splitDocuments);
        return ApiResult.success();
    }

}
