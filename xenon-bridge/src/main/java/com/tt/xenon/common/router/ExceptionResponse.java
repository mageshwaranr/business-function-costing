package com.tt.xenon.common.router;

import com.tt.xenon.common.error.XenonHttpRuntimeException;
import com.vmware.xenon.common.ServiceErrorResponse;

import java.util.ArrayList;
import java.util.Map;

import static com.vmware.xenon.common.Utils.buildKind;

/**
 * Created by mageshwaranr on 9/1/2016.
 */
public class ExceptionResponse extends ServiceErrorResponse {

  public static ServiceErrorResponse from(Throwable e) {
    if (e instanceof XenonHttpRuntimeException) {
      ExceptionResponse rsp = new ExceptionResponse();
      fillStacktrace(rsp, e);
      XenonHttpRuntimeException xenonHttpRuntimeException = (XenonHttpRuntimeException) e;
      rsp.errorCode = xenonHttpRuntimeException.getErrorCode();
      rsp.context = xenonHttpRuntimeException.getContext();
      rsp.documentKind = buildKind(ExceptionResponse.class);
      rsp.statusCode = 500;
      rsp.message = xenonHttpRuntimeException.getDeveloperMessage();
      rsp.stackTraceElements = xenonHttpRuntimeException.getStackTrace();
      return rsp;
    } else {
      return create(e, 500);
    }
  }

  public static void fillStacktrace(ServiceErrorResponse rsp, Throwable e) {
    rsp.stackTrace = new ArrayList<>();
    for (StackTraceElement se : e.getStackTrace()) {
      rsp.stackTrace.add(se.toString());
    }
  }

  public XenonHttpRuntimeException toError() {
    XenonHttpRuntimeException error = new XenonHttpRuntimeException(this.errorCode);
    if (this.context != null) {
      error.setContext(this.context);
    }
    error.setDeveloperMessage(this.message);
    if (this.stackTraceElements != null) {
      error.setStackTrace(this.stackTraceElements);
    }
    return error;
  }


  public int errorCode;
  public Map<String, Object> context;
  public StackTraceElement[] stackTraceElements;

}
