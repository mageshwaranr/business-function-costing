package com.tt.xenon.common.dns;

import com.tt.xenon.common.annotations.OperationBody;
import com.tt.xenon.common.dns.document.DnsState;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import static com.vmware.xenon.services.common.ServiceUriPaths.DNS;

/**
 * Created by mageshwaranr on 8/26/2016.
 */
@Path(DNS)
public interface XenonDnsService {

  @Path("/service-records")
  @POST
  DnsState registerService(@OperationBody DnsState state);

  @Path("/service-records/{name}")
  @GET
  DnsState findServiceByName(@PathParam("name") String name);

}
