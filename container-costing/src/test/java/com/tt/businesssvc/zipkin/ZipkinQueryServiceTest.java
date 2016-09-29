package com.tt.businesssvc.zipkin;

import com.tt.xenon.common.client.JaxRsServiceClient;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by mageshwaranr on 9/29/2016.
 */
public class ZipkinQueryServiceTest {

  private com.tt.businesssvc.zipkin.ZipkinQueryService svc;

  @Before
  public void setup() {
    svc = JaxRsServiceClient.newBuilder()
        .withBaseUri("http://10.112.80.92:9411")
        .withResourceInterface(ZipkinQueryService.class)
        .build();
  }

  @Test
  public void findAllServices() throws Exception {
    svc.findAllServices().forEach(System.out::println);
  }

  @Test
  public void findSpans() throws Exception {
    svc.findAllServices().forEach(name -> {
      System.out.println(svc.findSpans(name));
    });
  }

  @Test
  public void findTraces() throws Exception {
    System.out.println(svc.findTraces(10, "mashup-service",  "all"));
  }

}