package com.taobao.profile.utils;

public class VariableNotFoundException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -1103164294134360436L;

  public VariableNotFoundException() {
    super();
  }

  public VariableNotFoundException(String message, Throwable cause,
    boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public VariableNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public VariableNotFoundException(String message) {
    super(message);
  }

  public VariableNotFoundException(Throwable cause) {
    super(cause);
  }

}
