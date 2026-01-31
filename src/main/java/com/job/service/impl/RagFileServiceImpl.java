package com.job.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.job.entity.RagDocumentMapping;
import com.job.entity.RagFile;
import com.job.mapper.RagDocumentMappingMapper;
import com.job.mapper.RagFileMapper;
import com.job.service.RagFileService;
import com.job.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class RagFileServiceImpl extends ServiceImpl<RagFileMapper, RagFile> implements RagFileService {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private RagDocumentMappingMapper ragDocumentMappingMapper;

    private static final String UPLOAD_DIR = "uploads";  // 项目根目录下的uploads文件夹

    @Override
    @Transactional
    public boolean uploadAndEmbedFile(MultipartFile file, Long userId) throws Exception {
        // 检查文件类型
        if (!isSupportedFileType(file.getOriginalFilename())) {
            throw new IllegalArgumentException("不支持的文件类型: " + file.getOriginalFilename());
        }

        // 使用项目根目录下的uploads文件夹
        String uploadDir = UPLOAD_DIR;
        
        // 创建上传目录
        FileUtils.createDirectory(uploadDir);

        // 生成存储文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = FileUtils.getFileExtension(originalFilename);
        String storedFilename = UUID.randomUUID().toString() + fileExtension;
        String filePath = uploadDir + File.separator + storedFilename;

        // 保存文件到本地 - 使用流的方式避免Tomcat临时目录问题
        File destFile = new File(filePath);
        try (java.io.InputStream inputStream = file.getInputStream();
             java.io.FileOutputStream outputStream = new java.io.FileOutputStream(destFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        }

        // 使用Tika读取文档内容
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(new InputStreamResource(file.getInputStream()));

        // 配置较小的切片参数
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(200)        // 每个分块的token数量目标值
                .withMinChunkSizeChars(50) // 每个分块的最小字符数
                .withMaxNumChunks(50)      // 最大分块数量限制
                .withMinChunkLengthToEmbed(10) // 要包含的分块最小长度
                .build();

        List<Document> splitDocuments = splitter.apply(tikaDocumentReader.read());

        // 为每个文档片段添加元数据和可识别的ID，便于后续删除
        List<Document> updatedDocuments = new java.util.ArrayList<>();
        String baseDocId = "file_" + UUID.randomUUID().toString(); // 为整个文件生成基础ID
        for (int i = 0; i < splitDocuments.size(); i++) {
            Document doc = splitDocuments.get(i);
            String docId = baseDocId + "_part_" + i; // 为每个分片生成唯一ID
            Map<String, Object> metadata = new HashMap<>(doc.getMetadata());
            metadata.put("source_file_id", baseDocId); // 使用文件基础ID标识整个文件
            metadata.put("original_filename", originalFilename);
            metadata.put("upload_user_id", userId.toString());
            updatedDocuments.add(new Document(docId, Objects.requireNonNull(doc.getText()), metadata));
        }
        splitDocuments = updatedDocuments;

        // 将文档切片存入向量数据库
        if (!splitDocuments.isEmpty()) {
            vectorStore.add(splitDocuments);
        }

        // 保存文件信息到数据库
        RagFile ragFile = new RagFile();
        ragFile.setFilename(originalFilename);
        ragFile.setStoredFilename(storedFilename);
        ragFile.setFilePath(filePath);
        ragFile.setFileSize(file.getSize());
        ragFile.setFileType(fileExtension);
        ragFile.setUploadUserId(userId);
        ragFile.setChunkCount(splitDocuments.size());
        ragFile.setStatus(1); // 正常状态
        ragFile.setCreatedAt(LocalDateTime.now());
        ragFile.setUpdatedAt(LocalDateTime.now());

        boolean result = save(ragFile);
        
        // 保存文档ID到数据库，以便后续删除
        // 将文档ID与文件ID关联存储
        if (result) {
            // 保存文档ID映射记录
            try {
                for (Document doc : splitDocuments) {
                    RagDocumentMapping mapping = new RagDocumentMapping();
                    mapping.setFileId(ragFile.getId());
                    mapping.setDocumentId(doc.getId());
                    ragDocumentMappingMapper.insert(mapping);
                }
                log.info("文件 {} 成功上传，包含 {} 个文档片段", storedFilename, splitDocuments.size());
            } catch (Exception e) {
                log.warn("保存文档映射关系时发生错误（可能表未创建）: {}", e.getMessage());
                // 如果映射表不存在，不影响文件上传功能
            }
        }
        
        return result;
    }

    @Override
    @Transactional
    public boolean deleteFile(Long fileId, Long userId) throws Exception {
        // 查询文件信息
        RagFile ragFile = getById(fileId);
        if (ragFile == null || !ragFile.getUploadUserId().equals(userId)) {
            throw new IllegalArgumentException("文件不存在或无权限删除");
        }

        // 标记文件为已删除（软删除）
        ragFile.setStatus(0);
        ragFile.setUpdatedAt(LocalDateTime.now());
        boolean result = updateById(ragFile);

        // 从向量数据库中删除相关文档
        // 获取该文件对应的所有文档ID
        try {
            List<RagDocumentMapping> mappings = ragDocumentMappingMapper.findByFileId(ragFile.getId());
            if (!mappings.isEmpty()) {
                List<String> documentIds = new ArrayList<>();
                for (RagDocumentMapping mapping : mappings) {
                    documentIds.add(mapping.getDocumentId());
                }
                
                // 尝试从向量数据库中删除这些文档
                try {
                    vectorStore.delete(documentIds);  // Spring AI VectorStore接口支持删除指定ID的文档
                    log.info("已从向量数据库中删除 {} 个文档", documentIds.size());
                } catch (UnsupportedOperationException e) {
                    log.warn("当前向量存储不支持直接删除文档: {}", e.getMessage());
                    // 如果不支持删除，至少软删除映射记录
                }
                
                // 删除映射表中的记录
                ragDocumentMappingMapper.deleteByFileId(ragFile.getId());
            }
        } catch (Exception e) {
            log.warn("删除向量数据时发生错误（可能映射表未创建）: {}", e.getMessage());
            // 如果映射表不存在，不影响基本删除功能
        }

        // 同时删除本地文件
        if (result) {
            File fileToDelete = new File(ragFile.getFilePath());
            if (fileToDelete.exists()) {
                fileToDelete.delete();
            }
        }

        return result;
    }

    @Override
    public List<RagFile> getUserFiles(Long userId) {
        return lambdaQuery()
                .eq(RagFile::getUploadUserId, userId)
                .eq(RagFile::getStatus, 1)  // 只返回正常状态的文件
                .orderByDesc(RagFile::getCreatedAt)
                .list();
    }

    @Override
    public boolean isSupportedFileType(String fileName) {
        return FileUtils.isSupportedFileType(fileName);
    }
}