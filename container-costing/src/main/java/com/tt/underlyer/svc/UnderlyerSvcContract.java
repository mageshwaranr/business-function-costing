package com.tt.underlyer.svc;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.tt.container.entity.Underlyer;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
@Path(UnderlyerService.SELF_LINK)
public interface UnderlyerSvcContract {

  @Path("/all")
  @GET
  CompletableFuture<List<Underlyer>> findAll();

}
