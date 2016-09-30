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
public class DemandResource {
    private final Client client;

    public DemandResource(Client client) {
        this.client = Objects.requireNonNull(client);
    }

    @GET
    @Timed
    @Path("/order_flow")
    public Response fetchOrder(@Context HttpServletRequest request) throws InterruptedException {
        final Random random = new Random();
        TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
        return client.target("http://localhost:10003/order").request().get();
    }


    @GET
    @Timed
    @Path("/search/customer")
    public Response fetchCustomer(@Context HttpServletRequest request) throws InterruptedException {
        final Random random = new Random();
        TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
        return client.target("http://localhost:10001/customer").request().get();
    }



    @GET
    @Timed
    @Path("/search/catalog")
    public Response fetchCatalog(@Context HttpServletRequest request) throws InterruptedException {
        final Random random = new Random();
        TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
        return client.target("http://localhost:10002/catalog").request().get();
    }

}
