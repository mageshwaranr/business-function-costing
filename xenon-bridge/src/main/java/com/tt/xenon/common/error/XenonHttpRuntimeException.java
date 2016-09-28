package com.tt.xenon.common.error;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mageshwaranr on 8/9/2016.
 */
public class XenonHttpRuntimeException extends RuntimeException {

  // external facing error code
  // ui should be able to fetch a user friend message using this error-code
  // error code should follow pattern [M][C][E]
  // where M-> Module Code. Should be 2 digit and between 10 > M < 100
  //       C -> Client Response hint code,  1 for retry
  //       E -> Server side error code. Should be 2 digit and between 10 > M < 100
  private int errorCode;

  //Message useful for a developer to debug further. This is not for end-user consumption
  private String developerMessage;

  //Context information on when this error occurred. Can include input arguments, document self link etc.,
  private Map<String, Object> context = new HashMap<>();

  public XenonHttpRuntimeException(int errorCode) {
    this.errorCode = errorCode;
  }

  public XenonHttpRuntimeException(Throwable e, int errorCode) {
    super(e);
    this.errorCode = errorCode;
  }

  public XenonHttpRuntimeException(int errorCode, String developerMessage) {
    super(developerMessage);
    this.errorCode = errorCode;
    this.developerMessage = developerMessage;
  }

  public XenonHttpRuntimeException(Throwable e, int errorCode, String developerMessage) {
    super(developerMessage,e);
    this.errorCode = errorCode;
    this.developerMessage = developerMessage;
  }


  public XenonHttpRuntimeException(int errorCode, String developerMessage, Map<String, Object> context) {
    this.errorCode = errorCode;
    this.developerMessage = developerMessage;
    this.context = context;
  }

  public XenonHttpRuntimeException(Throwable e, int errorCode, String developerMessage, Map<String, Object> context) {
    super(e);
    this.errorCode = errorCode;
    this.developerMessage = developerMessage;
    this.context = context;
  }

  public int getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  public String getDeveloperMessage() {
    return developerMessage;
  }

  public void setDeveloperMessage(String developerMessage) {
    this.developerMessage = developerMessage;
  }

  public Map<String, Object> getContext() {
    return context;
  }

  public void setContext(Map<String, Object> context) {
    this.context = context;
  }

  /**
   * Builder style construct to add context information on when this error occured
   *
   * @param key
   * @param value
   * @return
   */
  public XenonHttpRuntimeException addToContext(String key, Object value) {
    this.context.put(key, value);
    return this;
  }

  @Override
  public String toString() {
    return "XenonHttpRuntimeException{" +
        "errorCode=" + errorCode +
        ", developerMessage='" + developerMessage + '\'' +
        ", context=" + context +
        '}';
  }
}
