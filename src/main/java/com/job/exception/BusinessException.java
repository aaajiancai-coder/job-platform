package com.job.exception;

import com.job.constants.ExceptionConstants;

// 业务异常类（使用常量）
public class BusinessException extends RuntimeException {
    private int code;

    public BusinessException() {
        super(ExceptionConstants.SYSTEM_UNKNOWN_ERROR_MSG);
        this.code = ExceptionConstants.SYSTEM_UNKNOWN_ERROR_CODE;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    // 快捷构造方法：直接使用常量
    public static BusinessException chatEmpty() {
        return new BusinessException(ExceptionConstants.BUSINESS_CHAT_EMPTY_CODE, ExceptionConstants.BUSINESS_CHAT_EMPTY_MSG);
    }

    public static BusinessException chatAccountDisable() {
        return new BusinessException(ExceptionConstants.BUSINESS_CHAT_ACCOUNT_DISABLE_CODE, ExceptionConstants.BUSINESS_CHAT_ACCOUNT_DISABLE_MSG);
    }

    public int getCode() {
        return code;
    }
}