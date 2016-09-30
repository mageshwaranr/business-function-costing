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
public class CustomerResource {
    private final Client client;

    public CustomerResource(Client client) {
        this.client = Objects.requireNonNull(client);
    }


    @GET
    @Timed
    @Path("/customer")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> fetchCustomer(@Context HttpServletRequest request) throws InterruptedException {
        final Random random = new Random();
        TimeUnit.MILLISECONDS.sleep(random.nextInt(500));
        List<String> customers = new ArrayList<>();
        customers.add("AAA");
        customers.add("BBB");
        return customers;
    }
}
