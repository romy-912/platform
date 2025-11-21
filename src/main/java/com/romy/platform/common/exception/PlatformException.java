package com.romy.platform.common.exception;



public class PlatformException extends GeneralException {

  protected static final String DEFAULT_CODE = "499";
  protected static final String DEFAULT_DETAILS = "BIZ_EXCEPTION";
  protected static final String DEFAULT_MESSAGE_KEY = "error.biz.message";

  public PlatformException() {
    super(DEFAULT_CODE, DEFAULT_DETAILS, DEFAULT_MESSAGE_KEY, null, null);
  }

  public PlatformException(String message) {
    super(DEFAULT_CODE, DEFAULT_DETAILS, DEFAULT_MESSAGE_KEY, message, null);
  }


  public PlatformException(String message, Object[] args) {
    super(DEFAULT_CODE, DEFAULT_DETAILS, DEFAULT_MESSAGE_KEY, args, message, null);
  }

  public PlatformException(String message, String arg) {
    super(DEFAULT_CODE, DEFAULT_DETAILS, DEFAULT_MESSAGE_KEY, new Object[]{arg}, message, null);
  }

}
