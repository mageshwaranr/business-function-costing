package com.tt.businesssvc;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;

/**
 * Created by mageshwaranr on 9/29/2016.
 */
public interface ServiceLineageContract {

  @Path("/function/all")
  @GET
  List<Node> findAllFunctions();

  @Path("/service/all")
  @GET
  List<Node> findAllServices();

  @Path("/function/{functionName}")
  @GET
  Node findLineageOfAFunction(@PathParam("functionName") String functionName);

}
