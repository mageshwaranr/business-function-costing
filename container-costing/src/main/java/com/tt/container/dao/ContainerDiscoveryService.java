package com.tt.container.dao;

import com.tt.container.entity.Container;
import com.tt.container.entity.Underlyer;
import com.tt.xenon.common.service.JaxRsBridgeStatelessService;

import javax.ws.rs.GET;
import java.util.List;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
public class ContainerDiscoveryService extends JaxRsBridgeStatelessService {


  @GET
  public List<Container> getContainers(Underlyer host) {

//    DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
//        .withDockerHost("tcp://my-docker-host.tld:2376")
//        .withDockerTlsVerify(true)
//        .withDockerCertPath("/home/user/.docker/certs")
//        .withDockerConfig("/home/user/.docker")
//        .withApiVersion("1.23")
//        .withRegistryUrl("https://index.docker.io/v1/")
//        .withRegistryUsername("dockeruser")
//        .withRegistryPassword("ilovedocker")
//        .withRegistryEmail("dockeruser@github.com")
//        .build();
//    DockerClient docker = DockerClientBuilder.getInstance(config).build();


    return null;
  }


}
