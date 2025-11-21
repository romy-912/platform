package com.romy.platform.common.exception;

import org.springframework.http.HttpStatus;


public class UnAuthorizationException extends GeneralException {

  protected static final String DEFAULT_CODE = HttpStatus.UNAUTHORIZED.name();
  protected static final String DEFAULT_DETAILS = HttpStatus.UNAUTHORIZED.series().name();
  protected static final String DEFAULT_MESSAGE_KEY = "error.unauth.message";

  public UnAuthorizationException() {
    this(null, null);
  }

  public UnAuthorizationException(String message) {
    this(message, null);
  }

  public UnAuthorizationException(String message, Throwable e) {
    super(DEFAULT_CODE, DEFAULT_DETAILS, DEFAULT_MESSAGE_KEY, message, e);
  }

}
