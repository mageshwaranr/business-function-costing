package com.example.helloworld;

import javax.ws.rs.client.Client;
import com.example.helloworld.resources.WarehouseResource;
import com.github.kristofa.brave.Brave;
import com.smoketurner.dropwizard.zipkin.ZipkinBundle;
import com.smoketurner.dropwizard.zipkin.ZipkinFactory;
import com.smoketurner.dropwizard.zipkin.client.ZipkinClientBuilder;
import com.smoketurner.dropwizard.zipkin.rx.BraveRxJavaSchedulersHook;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import rx.plugins.RxJavaPlugins;

public class WarehouseApplication
        extends Application<WarehouseConfiguration> {

    public static void main(String[] args) throws Exception {
        new WarehouseApplication().run(args);
    }

    @Override
    public String getName() {
        return "warehouse";
    }

    @Override
    public void initialize(Bootstrap<WarehouseConfiguration> bootstrap) {
        bootstrap.addBundle(
                new ZipkinBundle<WarehouseConfiguration>(getName()) {
                    @Override
                    public ZipkinFactory getZipkinFactory(
                            WarehouseConfiguration configuration) {
                        return configuration.getZipkinFactory();
                    }
                });
    }

    @Override
    public void run(WarehouseConfiguration configuration,
            Environment environment) throws Exception {

        final Brave brave = configuration.getZipkinFactory().build(environment);

        final Client client = new ZipkinClientBuilder(environment, brave)
                .build(configuration.getZipkinClient());

        RxJavaPlugins.getInstance()
                .registerSchedulersHook(new BraveRxJavaSchedulersHook(brave));

        // Register resources
        final WarehouseResource resource = new WarehouseResource(client);
        environment.jersey().register(resource);
    }
}
