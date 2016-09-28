package com.tt.underlyer.svc;

import com.tt.container.entity.Underlyer;
import com.tt.xenon.common.client.ServiceClientUtil;
import com.tt.xenon.common.query.XenonQueryService;
import com.tt.xenon.common.router.RequestRouterBuilder;
import com.vmware.xenon.common.*;
import com.vmware.xenon.services.common.QueryTask;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.vmware.xenon.common.ServiceDocument.FIELD_NAME_KIND;

/**
 * Created by mageshwaranr on 9/28/2016.
 */
public class UnderlyerService extends StatelessService {


  public static final String SELF_LINK = "/business/function/svc/underlyer";

  public UnderlyerService() {
    super.toggleOption(Service.ServiceOption.URI_NAMESPACE_OWNER, true);
  }

  private XenonQueryService queryService;

  @Override
  public OperationProcessingChain getOperationProcessingChain() {
    if (super.getOperationProcessingChain() != null) {
      return super.getOperationProcessingChain();
    }
    final OperationProcessingChain opProcessingChain = new OperationProcessingChain(this);
    RequestRouter requestRouter = RequestRouterBuilder.parseJaxRsAnnotations(this);
    opProcessingChain.add(requestRouter);
    setOperationProcessingChain(opProcessingChain);

    if(queryService == null)
      queryService = ServiceClientUtil.newQueryServiceClient(this.getHost());
    return opProcessingChain;
  }

  @Path("/all")
  @GET
  public CompletableFuture<List<Underlyer>> findAll() {

    QueryTask.Query accountNameQuery = QueryTask.Query.Builder.create()
        .addFieldClause(FIELD_NAME_KIND, Utils.buildKind(Underlyer.class))
        .build();

    QueryTask queryTask = QueryTask.Builder.createDirectTask().setQuery(accountNameQuery).
        addOption(QueryTask.QuerySpecification.QueryOption.EXPAND_CONTENT).
        build();

    CompletableFuture<QueryTask> queryTaskCompletableFuture = queryService.postQueryTask(queryTask);

    return queryTaskCompletableFuture
        .thenApply(qTaskCompleted -> qTaskCompleted.results.documents.entrySet()
            .stream()
            .map( entry -> Utils.fromJson(Utils.toJson(entry.getValue()), Underlyer.class))
            .collect(Collectors.toList()));
  }



}
