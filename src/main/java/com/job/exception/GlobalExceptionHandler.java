package com.job.exception;


import com.alibaba.fastjson.JSONObject;
import com.job.common.api.ApiResult;
import com.job.constants.ExceptionConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ========== 新增：捕获前端主动断开连接的异常 ==========
    @ExceptionHandler({ClientAbortException.class, AsyncRequestNotUsableException.class})
    public void handleClientAbortException(Exception e) {
        // 仅打印 DEBUG 日志，不抛出、不处理（用户主动断开，无需关注）
        log.debug("【客户端主动断开连接】- {}", e.getMessage());
    }

    // ========== 2. 处理 Spring Security 权限异常 ==========
    @ExceptionHandler(AuthorizationDeniedException.class)
    public void handleAuthorizationDeniedException(AuthorizationDeniedException e, HttpServletResponse response) throws IOException {
        log.error("【权限拒绝异常】- {}", e.getMessage(), e);
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        ApiResult<Void> result = ApiResult.error(2001, "无权限访问该接口");
        response.getWriter().write(JSONObject.toJSONString(result));
    }

    // ========== 3. 处理所有未捕获的异常（区分 SSE/普通接口） ==========
    @ExceptionHandler(Exception.class)
    public void handleAllUncaughtException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestURI = request.getRequestURI();
        // 判断是否是 SSE 接口
        boolean isSseRequest = requestURI.contains("/api/ai/")
                || (request.getHeader("Accept") != null && request.getHeader("Accept").contains("text/event-stream"));

        if (isSseRequest) {
            // SSE 接口：返回 text/event-stream 格式的错误
            log.error("【SSE 接口异常】- URI:{}", requestURI, e);
            response.setContentType("text/event-stream;charset=UTF-8");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            PrintWriter writer = response.getWriter();
            // SSE 标准格式：event + data
            writer.write("event: error\n");
            writer.write("data: " + JSONObject.toJSONString(ApiResult.error(500, "AI 回复异常，请稍后再试")) + "\n\n");
            writer.flush();
            writer.close();
        } else {
            // 普通接口：返回 JSON 格式
            log.error("【全局异常】- URI:{}, 未知异常", requestURI, e);
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            ApiResult<Void> result = ApiResult.error(500, "服务器内部异常");
            response.getWriter().write(JSONObject.toJSONString(result));
        }
    }



    // 权限不足异常
    @ExceptionHandler(AccessDeniedException.class)
    public ApiResult<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.error("【权限异常】- {}", e.getMessage(), e);
        return ApiResult.error(ExceptionConstants.AUTH_FORBIDDEN_CODE, ExceptionConstants.AUTH_FORBIDDEN_MSG);
    }

    // 类型转换异常
    @ExceptionHandler(ClassCastException.class)
    public ApiResult<Void> handleClassCastException(ClassCastException e) {
        log.error("【类型转换异常】- {}", e.getMessage(), e);
        return ApiResult.error(ExceptionConstants.SYSTEM_CLASS_CAST_ERROR_CODE, ExceptionConstants.SYSTEM_CLASS_CAST_ERROR_MSG);
    }

    // 空指针异常
    @ExceptionHandler(NullPointerException.class)
    public ApiResult<Void> handleNullPointerException(NullPointerException e) {
        log.error("【空指针异常】- {}", e.getMessage(), e);
        return ApiResult.error(ExceptionConstants.SYSTEM_NULL_POINTER_ERROR_CODE, ExceptionConstants.SYSTEM_NULL_POINTER_ERROR_MSG);
    }

    // 404异常
    @ExceptionHandler(NoHandlerFoundException.class)
    public ApiResult<Void> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("【404异常】- 接口不存在：{}", e.getRequestURL());
        return ApiResult.error(ExceptionConstants.SYSTEM_NOT_FOUND_ERROR_CODE, ExceptionConstants.SYSTEM_NOT_FOUND_ERROR_MSG);
    }


    // 自定义业务异常
    @ExceptionHandler(BusinessException.class)
    public ApiResult<Void> handleBusinessException(BusinessException e) {
        log.warn("【业务异常】- {}", e.getMessage());
        return ApiResult.error(e.getCode(), e.getMessage());
    }

    // runtime 异常
    @ExceptionHandler(RuntimeException.class)
    public ApiResult<Void> handleRuntimeException(RuntimeException e) {
        log.error("【运行时异常】- {}", e.getMessage(), e);
        return ApiResult.error(ExceptionConstants.SYSTEM_UNKNOWN_ERROR_CODE, ExceptionConstants.SYSTEM_UNKNOWN_ERROR_MSG);
    }

}