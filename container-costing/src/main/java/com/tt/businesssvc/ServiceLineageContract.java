package com.tt.businesssvc;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;

/**
 * Created by mageshwaranr on 9/29/2016.
 */
@Path(ServiceLineage.SELF_LINK)
public interface ServiceLineageContract {

  @Path("/all")
  @GET
  List<Node> findAllFunctions();

  @Path("/lineage/function/{functionName}")
  @GET
  Node findLineageOfAFunction(@PathParam("functionName") String functionName);

}
