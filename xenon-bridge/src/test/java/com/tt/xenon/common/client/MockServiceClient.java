package com.tt.xenon.common.client;

import com.tt.xenon.common.annotations.OperationBody;
import com.tt.xenon.common.annotations.PATCH;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * Created by mageshwaranr on 8/22/2016.
 */
@Path("/vrbc/xenon/util/test")
public interface MockServiceClient {

  @GET
  @Path("/get/{path}")
  Map<String, String> getAction(@QueryParam("query") String query, @PathParam("path") String path);

  @PATCH
  @Path("/patch")
  Map<String, String> patchAction(@OperationBody List<Integer> contents);

  @POST
  @Path("/post")
  Map<String, String> postAction(@OperationBody List<String> contents);


  @POST
  @Path("/post")
  Response postActionWithReturnResponse(@OperationBody List<String> contents);

  @GET
  @Path("/get/genericReturn")
  Map<String, SuccessResponse> getActionWithGenericReturn();

  @DELETE
  @Path("/delete/{path}")
  List<String> deleteAction(@PathParam("path") String path);

  @POST
  @Path("/post/auth")
  List<String> postActionWithAuthInfo(@HeaderParam("header") String header,
                                      @CookieParam("cookie") String cookie, @OperationBody List<Integer> contents);


  class SuccessResponse {
    private int code;
    private String message;

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
  }
}
