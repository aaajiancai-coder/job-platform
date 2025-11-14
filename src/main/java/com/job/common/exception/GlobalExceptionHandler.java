package com.job.common.exception;

import com.job.common.api.ApiResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResult<Void> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ApiResult.failed(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResult<Void> handleException(Exception ex) {
        // 记录日志
        ex.printStackTrace();
        return ApiResult.failed("服务器异常: " + ex.getMessage());
    }
}
