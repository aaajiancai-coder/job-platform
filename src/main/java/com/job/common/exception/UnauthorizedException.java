package com.job.common.exception;

public class UnauthorizedException extends ServiceException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
