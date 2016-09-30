package com.example.helloworld;

import javax.ws.rs.client.Client;
import com.example.helloworld.resources.DemandResource;
import com.github.kristofa.brave.Brave;
import com.smoketurner.dropwizard.zipkin.ZipkinBundle;
import com.smoketurner.dropwizard.zipkin.ZipkinFactory;
import com.smoketurner.dropwizard.zipkin.client.ZipkinClientBuilder;
import com.smoketurner.dropwizard.zipkin.rx.BraveRxJavaSchedulersHook;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import rx.plugins.RxJavaPlugins;

public class DemandApplication
        extends Application<DemandConfiguration> {

    public static void main(String[] args) throws Exception {
        new DemandApplication().run(args);
    }

    @Override
    public String getName() {
        return "Demand_management";
    }

    @Override
    public void initialize(Bootstrap<DemandConfiguration> bootstrap) {
        bootstrap.addBundle(
                new ZipkinBundle<DemandConfiguration>(getName()) {
                    @Override
                    public ZipkinFactory getZipkinFactory(
                            DemandConfiguration configuration) {
                        return configuration.getZipkinFactory();
                    }
                });
    }

    @Override
    public void run(DemandConfiguration configuration,
            Environment environment) throws Exception {

        final Brave brave = configuration.getZipkinFactory().build(environment);

        final Client client = new ZipkinClientBuilder(environment, brave)
                .build(configuration.getZipkinClient());

        RxJavaPlugins.getInstance()
                .registerSchedulersHook(new BraveRxJavaSchedulersHook(brave));

        // Register resources
        final DemandResource resource = new DemandResource(client);
        environment.jersey().register(resource);
    }
}
