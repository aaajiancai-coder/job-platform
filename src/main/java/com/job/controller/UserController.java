package com.job.controller;

import com.job.common.api.ApiResult;
import com.job.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;

    /**
     * 上传头像
     */
    @PostMapping("/{userId}/avatar")
    public ApiResult<String> uploadAvatar(
            @PathVariable Long userId,
            @RequestParam("avatar") MultipartFile file,
            HttpServletRequest request) {
        boolean result = userService.updateUserAvatar(userId, file, request);
        return result ? ApiResult.success("头像上传成功") : ApiResult.error("头像上传失败");

    }

    /**
     * 获取头像
     */
    @GetMapping("/{userId}/avatar")
    public ApiResult<byte[]> getAvatar(
            @PathVariable Long userId,
            HttpServletRequest request) throws IOException {

        File avatarFile = userService.getUserAvatarFile(userId, request);
        if (!avatarFile.exists()) {
            return ApiResult.error("头像不存在");
        }

        byte[] fileContent = Files.readAllBytes(avatarFile.toPath());
        String filename = avatarFile.getName();
        String contentType = determineContentType(filename);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDispositionFormData("inline", filename);

        return ApiResult.success(fileContent);
    }

    /**
     * 删除头像
     */
    @DeleteMapping("/{userId}/avatar")
    public ApiResult<String> deleteAvatar(@PathVariable Long userId, HttpServletRequest request) {
        boolean success = userService.deleteUserAvatar(userId, request);
        if (success) {
            return ApiResult.success("头像删除成功");
        } else {
            return ApiResult.error("头像删除失败");
        }
    }

    private String determineContentType(String filename) {
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
        if (filename.endsWith(".gif")) return "image/gif";
        return "application/octet-stream";
    }
}
