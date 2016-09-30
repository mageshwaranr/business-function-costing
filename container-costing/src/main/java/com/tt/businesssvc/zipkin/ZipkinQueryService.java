package com.tt.businesssvc.zipkin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Created by mageshwaranr on 9/29/2016.
 */
@Path("/api/v1")
public interface ZipkinQueryService {

  @Path("/services")
  @GET
  List<String> findAllServices();

  @Path("/spans")
  @GET
  List<String> findSpans(@QueryParam("serviceName") String svcName);

  @Path("/traces")
  @GET
  List<List<Trace>> findTraces(@QueryParam("limit") int limit, @QueryParam("serviceName") String svcName, @QueryParam("spanName") String spanName);

  @Path("/dependencies")
  @GET
  List<Dependency> findDependency(@QueryParam("endTs") long endTs) ;
}
