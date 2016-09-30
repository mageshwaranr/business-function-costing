package com.example.helloworld.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;

@Path("/")
public class CatalogResource {
    private final Client client;

    public CatalogResource(Client client) {
        this.client = Objects.requireNonNull(client);
    }

    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/catalog")
    public List<String> fetchCatalog(@Context HttpServletRequest request) throws InterruptedException {
        final Random random = new Random();
        TimeUnit.MILLISECONDS.sleep(random.nextInt(500));
        List<String> catalog = new ArrayList<>();
        catalog.add("AAA");
       catalog.add("BBB");
        return catalog;
    }


    @GET
    @Timed
    @Path("/customer")
    public Response fetchCustomer(@Context HttpServletRequest request) throws InterruptedException {
        final Random random = new Random();
        TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
        return client.target("http://localhost:8080/catalog").request().get();
    }


    @GET
    @Timed
    @Path("/result")
    public Response result() throws InterruptedException {
        final Random random = new Random();
        TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
        return Response.noContent().build();
    }
}
