package com.job.exception;

public class UnauthorizedException extends ServiceException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
