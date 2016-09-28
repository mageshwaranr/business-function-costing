package com.tt.container.dao;

import java.net.URI;
import java.util.List;

import javax.ws.rs.GET;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.tt.container.entity.Underlyer;
import com.tt.xenon.common.service.JaxRsBridgeStatelessService;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
public class ContainerDiscoveryService extends JaxRsBridgeStatelessService {


  @GET
  public List<Container> getContainers(List<Underlyer> host) throws DockerException, InterruptedException {

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
	final DockerClient docker = DefaultDockerClient.builder()
	      .uri(URI.create("http://10.112.80.92:2375"))
	      .build();
	final List<Container> containers = docker.listContainers();
    return containers;
  }

}
