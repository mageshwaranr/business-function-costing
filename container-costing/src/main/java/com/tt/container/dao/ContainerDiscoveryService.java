package com.tt.container.dao;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.tt.container.entity.Underlyer;
import com.tt.underlyer.svc.UnderlyerSvcContract;
import com.tt.xenon.common.client.JaxRsServiceClient;
import com.tt.xenon.common.error.XenonHttpRuntimeException;
import com.tt.xenon.common.service.JaxRsBridgeStatelessService;
import com.vmware.xenon.common.OperationProcessingChain;

import static com.tt.CostServicesInfo.UNDERLYER_SVC;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
public class ContainerDiscoveryService extends JaxRsBridgeStatelessService {


  public static final String SELF_LINK = "/business/function/svc/discovery/container";


  private UnderlyerSvcContract underlyerSvcContract;

  @Override
  public OperationProcessingChain getOperationProcessingChain() {
    if(underlyerSvcContract == null){
      underlyerSvcContract = JaxRsServiceClient.newBuilder()
          .withHost(this.getHost())
          .withResourceInterface(UnderlyerSvcContract.class)
          .withServiceInfo(UNDERLYER_SVC)
          .build();
    }
   return super.getOperationProcessingChain();
  }

  List<Container> findContainers(Underlyer host) {
	final DockerClient docker = DefaultDockerClient.builder()
	      .uri(URI.create("http://"+host.id))
	      .build();
    final List<Container> containers;
    try {
      containers = docker.listContainers();
    } catch (Exception e) {
      throw new XenonHttpRuntimeException(500,"Unable to find container info for given host");
    }
    return containers;
  }

  @Path("/all")
  @GET
  public CompletableFuture<List<Container>> findAll() {
    return underlyerSvcContract.findAll()
        .thenApply(underlyers -> underlyers
            .stream()
            .flatMap(underlyer -> findContainers(underlyer).stream())
            .collect(Collectors.toList()));
  }

}
