package com.tt.underlyer.svc;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.tt.underlyer.entity.Underlyer;

import static com.tt.CostServicesInfo.UNDERLYER_SVC;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
public interface UnderlyerSvcContract {

  @Path("/all")
  @GET
  CompletableFuture<List<Underlyer>> findAll();

}
