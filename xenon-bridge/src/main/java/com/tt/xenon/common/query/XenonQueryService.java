package com.tt.xenon.common.query;

import com.tt.xenon.common.annotations.OperationBody;
import com.vmware.xenon.services.common.QueryTask;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import java.util.concurrent.CompletableFuture;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by mageshwaranr on 9/1/2016.
 */
@Path("/core/query-tasks")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public interface XenonQueryService {

  @POST
  CompletableFuture<QueryTask> postQueryTask(@OperationBody QueryTask task);

}
