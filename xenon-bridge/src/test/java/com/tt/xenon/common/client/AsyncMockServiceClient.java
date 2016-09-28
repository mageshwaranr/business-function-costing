package com.tt.xenon.common.client;

import com.tt.xenon.common.annotations.OperationBody;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by mageshwaranr on 8/22/2016.
 */
@Path("/vrbc/xenon/util/test")
public interface AsyncMockServiceClient {

  @GET
  @Path("/get/{path}")
  CompletableFuture<Map<String, String>> getAction(@QueryParam("query") String query, @PathParam("path") String path);

  @POST
  @Path("/post")
  CompletableFuture<Void> postAction(@OperationBody List<String> contents);


  @POST
  @Path("/post/auth")
  CompletableFuture<List<String>> postActionWithAuthInfo(@HeaderParam("header") String header,
                                                         @CookieParam("cookie") String cookie, @OperationBody List<Integer> contents);


  @POST
  @Path("/post")
  CompletableFuture<Response> postActionWithReturnResponse(@OperationBody List<String> contents);

  @GET
  @Path("/get/genericReturn")
  CompletableFuture<Map<String, SuccessResponse>> getActionWithGenericReturn();

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

