package com.job.controller;

import com.job.common.api.ApiResult;
import com.job.entity.RagFile;
import com.job.service.RagFileService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/vector")
public class VectorController {

    @Autowired
    private RagFileService ragFileService;

    /**
     * 上传文件并嵌入向量数据库
     *
     * @param file 待嵌入的文件
     * @param userId 上传用户ID
     * @return 是否成功
     */
    @SneakyThrows
    @PostMapping("embedding")
    public ApiResult embedding(@RequestParam MultipartFile file, 
                              @RequestParam Long userId) {
        boolean result = ragFileService.uploadAndEmbedFile(file, userId);
        if (result) {
            return ApiResult.success("文件上传并嵌入成功");
        } else {
            return ApiResult.failed("文件上传失败");
        }
    }

    /**
     * 删除文件及其向量数据
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 是否成功
     */
    @DeleteMapping("file/{fileId}")
    public ApiResult deleteFile(@PathVariable Long fileId, 
                               @RequestParam Long userId) {
        try {
            boolean result = ragFileService.deleteFile(fileId, userId);
            if (result) {
                return ApiResult.success("文件删除成功");
            } else {
                return ApiResult.failed("文件删除失败");
            }
        } catch (Exception e) {
            return ApiResult.failed(e.getMessage());
        }
    }

    /**
     * 获取用户上传的文件列表
     *
     * @param userId 用户ID
     * @return 文件列表
     */
    @GetMapping("files")
    public ApiResult getUserFiles(@RequestParam Long userId) {
        List<RagFile> files = ragFileService.getUserFiles(userId);
        return ApiResult.success(files);
    }

    /**
     * 检查文件类型是否支持
     *
     * @param fileName 文件名
     * @return 是否支持
     */
    @GetMapping("check-file-type")
    public ApiResult checkFileType(@RequestParam String fileName) {
        boolean supported = ragFileService.isSupportedFileType(fileName);
        return ApiResult.success(supported);
    }
}
