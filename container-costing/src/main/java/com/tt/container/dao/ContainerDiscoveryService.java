package com.tt.container.dao;

import static com.tt.CostServicesInfo.UNDERLYER_SVC;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerStats;
import com.tt.container.entity.Underlyer;
import com.tt.underlyer.svc.UnderlyerSvcContract;
import com.tt.xenon.common.client.JaxRsServiceClient;
import com.tt.xenon.common.error.XenonHttpRuntimeException;
import com.tt.xenon.common.service.JaxRsBridgeStatelessService;
import com.vmware.xenon.common.OperationProcessingChain;

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

  List<com.tt.container.entity.Container> containerCost(Underlyer host) {
	  
	  Double computeCost;
	  List<com.tt.container.entity.Container> returnContainerList = new ArrayList<com.tt.container.entity.Container>();
	  ContainerStats stats;
	  
	  final List<Container> containerList;

	  final DockerClient docker = DefaultDockerClient.builder()
		      .uri(URI.create("http://"+host.id))
		      .build();	  
	  try {
		  com.tt.container.entity.Container tempReturnContainer;
	      containerList = docker.listContainers();
	      for (Container container : containerList ) {
	    	  stats = docker.stats(container.id());
	    	  computeCost = ((double)(stats.cpuStats().cpuUsage().totalUsage() - stats.precpuStats().cpuUsage().totalUsage())/((double)(stats.cpuStats().systemCpuUsage() - stats.precpuStats().systemCpuUsage())));
	    	  tempReturnContainer = new com.tt.container.entity.Container();
	    	  tempReturnContainer.ip=container.networkSettings().ipAddress();
	    	  tempReturnContainer.name=container.imageId();
	    	  tempReturnContainer.image=container.image();
	    	  tempReturnContainer.cost=(computeCost*(host.cost));
	    	  returnContainerList.add(tempReturnContainer);
	    	  
	      }
	      
	    } catch (Exception e) {
	      throw new XenonHttpRuntimeException(500,"Unable to find container info for given host");
	    } 
	  return returnContainerList;
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
  
  @Path("/costs")
  @GET
  public CompletableFuture<List<com.tt.container.entity.Container>> containerCost(){
	 
	  return underlyerSvcContract.findAll()
	  .thenApply( underlyers -> {
		  return underlyers.stream()
		  .flatMap(underlyer -> containerCost(underlyer).stream())
		  .collect(Collectors.toList());
	  });
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
