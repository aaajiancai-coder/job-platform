package com.job.common.api;

import java.io.Serializable;

public class ApiResult<T> implements Serializable {
    private int code;
    private String message;
    private T data;
    
    private static final int SUCCESS_CODE = 200;
    private static final int ERROR_CODE = 500;
    private static final String SUCCESS_MESSAGE = "操作成功";
    private static final String ERROR_MESSAGE = "操作失败";

    public ApiResult() {}

    public ApiResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResult<T> success() {
        return new ApiResult<>(SUCCESS_CODE, SUCCESS_MESSAGE, null);
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(SUCCESS_CODE, message, data);
    }

    public static <T> ApiResult<T> failed() {
        return new ApiResult<>(ERROR_CODE, ERROR_MESSAGE, null);
    }

    public static <T> ApiResult<T> failed(String message) {
        return new ApiResult<>(ERROR_CODE, message, null);
    }

    public static <T> ApiResult<T> failed(int code, String message) {
        return new ApiResult<>(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
