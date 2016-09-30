package com.example.helloworld.resources;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;

@Path("/")
public class SupplyChainResource {
    private final Client client;

    public SupplyChainResource(Client client) {
        this.client = Objects.requireNonNull(client);
    }

    @GET
    @Timed
    @Path("/dispatch")
    public Response fetchDispatch(@Context HttpServletRequest request) throws InterruptedException {
        client.target("http://localhost:10005/request_pickup").request().get();
        return client.target("http://localhost:10006/process_order").request().get();
    }

}
