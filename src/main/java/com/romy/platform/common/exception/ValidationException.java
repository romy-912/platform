package com.romy.platform.common.exception;

import org.springframework.http.HttpStatus;


public class ValidationException extends GeneralException {

    protected static final String DEFAULT_CODE = HttpStatus.BAD_REQUEST.name();
    protected static final String DEFAULT_DETAILS = HttpStatus.BAD_REQUEST.series().name();
    protected static final String DEFAULT_MESSAGE_KEY = "error.invalid.message";

    public ValidationException() {
        super(DEFAULT_CODE, DEFAULT_DETAILS, DEFAULT_MESSAGE_KEY, null, null);
    }

    public ValidationException(String message) {
        super(DEFAULT_CODE, DEFAULT_DETAILS, DEFAULT_MESSAGE_KEY, message, null);
    }

    public ValidationException(String message, Throwable e) {
        super(DEFAULT_CODE, DEFAULT_DETAILS, DEFAULT_MESSAGE_KEY, message, e);
    }

    public ValidationException(String message, String arg) {
        super(DEFAULT_CODE, DEFAULT_DETAILS, DEFAULT_MESSAGE_KEY, new Object[]{arg}, message, null);
    }

}
