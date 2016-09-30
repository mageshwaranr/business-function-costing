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
public class LogisticsResource {
    private final Client client;

    public LogisticsResource(Client client) {
        this.client = Objects.requireNonNull(client);
    }

    @GET
    @Timed
    @Path("/request_pickup")
    public Response fetchCatalog(@Context HttpServletRequest request) throws InterruptedException {
        final Random random = new Random();
        TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
        return Response.ok().build();
    }
}
