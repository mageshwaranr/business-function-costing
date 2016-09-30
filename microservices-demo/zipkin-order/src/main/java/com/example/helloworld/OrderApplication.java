package com.example.helloworld;

import javax.ws.rs.client.Client;
import com.example.helloworld.resources.OrderResource;
import com.github.kristofa.brave.Brave;
import com.smoketurner.dropwizard.zipkin.ZipkinBundle;
import com.smoketurner.dropwizard.zipkin.ZipkinFactory;
import com.smoketurner.dropwizard.zipkin.client.ZipkinClientBuilder;
import com.smoketurner.dropwizard.zipkin.rx.BraveRxJavaSchedulersHook;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import rx.plugins.RxJavaPlugins;

public class OrderApplication
        extends Application<OrderConfiguration> {

    public static void main(String[] args) throws Exception {
        new OrderApplication().run(args);
    }

    @Override
    public String getName() {
        return "Order";
    }

    @Override
    public void initialize(Bootstrap<OrderConfiguration> bootstrap) {
        bootstrap.addBundle(
                new ZipkinBundle<OrderConfiguration>(getName()) {
                    @Override
                    public ZipkinFactory getZipkinFactory(
                            OrderConfiguration configuration) {
                        return configuration.getZipkinFactory();
                    }
                });
    }

    @Override
    public void run(OrderConfiguration configuration,
            Environment environment) throws Exception {

        final Brave brave = configuration.getZipkinFactory().build(environment);

        final Client client = new ZipkinClientBuilder(environment, brave)
                .build(configuration.getZipkinClient());

        RxJavaPlugins.getInstance()
                .registerSchedulersHook(new BraveRxJavaSchedulersHook(brave));

        // Register resources
        final OrderResource resource = new OrderResource(client);
        environment.jersey().register(resource);
    }
}
