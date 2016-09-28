package com.tt.xenon.common.router;

import com.tt.xenon.common.annotations.OperationBody;
import com.tt.xenon.common.annotations.PATCH;
import com.vmware.xenon.common.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.vmware.xenon.common.Utils.toJson;

/**
 * Created by mageshwaranr on 8/17/2016.
 */
public class MockedStatelessService extends StatelessService {

  public static final String SELF_LINK = "/vrbc/common/routing/test";

  public MockedStatelessService() {
    super.toggleOption(Service.ServiceOption.URI_NAMESPACE_OWNER, true);
  }

  @Override
  public OperationProcessingChain getOperationProcessingChain() {
    if (super.getOperationProcessingChain() != null) {
      return super.getOperationProcessingChain();
    }
    final OperationProcessingChain opProcessingChain = new OperationProcessingChain(this);
    RequestRouter requestRouter = RequestRouterBuilder.parseJaxRsAnnotations(this);
    opProcessingChain.add(requestRouter);
    setOperationProcessingChain(opProcessingChain);
    return opProcessingChain;
  }


  @Path("/simple")
  @GET
  public void simpleGet(final Operation get) {
    Map<String, String> help = new LinkedHashMap<>();
    help.put("result", "success");
    get.setBody(help);
    get.complete();
  }

  @Path("/simple")
  @PATCH
  public void simplePatch(final Operation get) {
    Map<String, String> help = new LinkedHashMap<>();
    help.put("result", "success");
    get.setBody(help);
    get.complete();
  }


  @Path("/path/{pathParam}/query")
  @GET
  public void getWithQueryAndPath(final @PathParam("pathParam") String pathValue,
                                  final @QueryParam("queryParam") String query, final Operation get) {
    Map<String, String> help = new LinkedHashMap<>();
    help.put("result", "success");
    help.put("pathParam", pathValue);
    help.put("queryParam", query);
    get.setBody(help);
    get.complete();
  }

  @Path("/path/{pathParam}/query")
  @POST
  public void postWithQueryAndPath(final @PathParam("pathParam") String pathValue,
                                   final @QueryParam("queryParam") String query,
                                   final @OperationBody List<String> payload,
                                   final Operation get) {
    Map<String, String> help = new LinkedHashMap<>();
    help.put("result", "success");
    help.put("pathParam", pathValue);
    help.put("queryParam", query);
    help.put("body", toJson(payload));
    get.setBody(help);
    get.complete();
  }

  @Path("/complete/path/{pathParam}")
  @GET
  public Map<String, String> getWithQueryAndPathAndReturn(@PathParam("pathParam") String pathValue, @QueryParam("queryParam") String query) {
    Map<String, String> help = new LinkedHashMap<>();
    help.put("result", "success");
    help.put("pathParam", pathValue);
    help.put("queryParam", query);
    return help;
  }

  @Path("/complete/path/{pathParam}")
  @PUT
  public Map<String, String> putWithQueryAndPathAndReturn(final @PathParam("pathParam") String pathValue,
                                                          final @QueryParam("queryParam") String query,
                                                          final @OperationBody List<String> payload) {
    Map<String, String> help = new LinkedHashMap<>();
    help.put("result", "success");
    help.put("pathParam", pathValue);
    help.put("queryParam", query);
    help.put("body", toJson(payload));
    return help;
  }


  @Path("/validation/path/{pathParam}")
  @POST
  public Map<String, String> postWithModelValidationOnBody(final @PathParam("pathParam") String pathValue,
                                                           final @QueryParam("queryParam") String query,
                                                           final @OperationBody EmployeePojoForTest payload) {
    Map<String, String> help = new LinkedHashMap<>();
    help.put("result", "success");
    help.put("pathParam", pathValue);
    help.put("queryParam", query);
    help.put("body", toJson(payload));
    return help;
  }

  public static class EmployeePojoForTest {

    @NotNull
    private String name;
    @Min(18)
    @Max(100)
    private int age;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public int getAge() {
      return age;
    }

    public void setAge(int age) {
      this.age = age;
    }

  }

}
