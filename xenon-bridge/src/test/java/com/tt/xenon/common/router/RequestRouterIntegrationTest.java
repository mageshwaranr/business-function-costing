package com.tt.xenon.common.router;

import com.tt.xenon.common.VrbcServiceInfo;
import com.tt.xenon.common.client.JaxRsServiceClient;
import com.tt.xenon.common.host.XenonHost;
import com.vmware.xenon.common.ServiceHost;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.Map;

import static com.vmware.xenon.common.UriUtils.buildUri;
import static org.junit.Assert.assertEquals;

/**
 * Created by mageshwaranr on 9/1/2016.
 */
public class RequestRouterIntegrationTest {

  private static XenonHost host;
  private static int port = 9999;
  private static TemporaryFolder tmp = new TemporaryFolder();

  private MockedStatelessServiceContract serviceProxy;

  @BeforeClass
  public static void startHost() throws Throwable {
    tmp.create();
    ServiceHost.Arguments args = new ServiceHost.Arguments();
    args.port = port;
    args.sandbox = tmp.getRoot().toPath();
    host = XenonHost.newBuilder()
        .withArguments(args)
        .withService(new VrbcServiceInfo() {
          @Override
          public String serviceLink() {
            return MockedStatelessService.SELF_LINK;
          }

          @Override
          public String serviceName() {
            return "MockedStatelessService";
          }
        }, new MockedStatelessService())
        .buildAndStart();
  }

  @Before
  public void init() {
    String uri = "http://localhost:" + port;
    serviceProxy = JaxRsServiceClient.newProxy(MockedStatelessServiceContract.class, buildUri(uri));
  }


  @Test
  public void testGetWithQueryAndPathAndReturn() throws Throwable {
    Map<String, String> body = serviceProxy.getWithQueryAndPathAndReturn("value_of_path_param", "value_of_query_param");
    assertEquals("success", body.get("result"));
    assertEquals("value_of_path_param", body.get("pathParam"));
    assertEquals("value_of_query_param", body.get("queryParam"));
  }

  @AfterClass
  public static void stopHost() {
    if (host != null) {
      host.stop();
    }
    tmp.delete();
  }

  @Path(MockedStatelessService.SELF_LINK)
  public interface MockedStatelessServiceContract {

    @Path("/complete/path/{pathParam}")
    @GET
    Map<String, String> getWithQueryAndPathAndReturn(@PathParam("pathParam") String pathValue,
                                                     @QueryParam("queryParam") String query);

  }
}
