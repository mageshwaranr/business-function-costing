package com.example.helloworld;

import javax.ws.rs.client.Client;
import com.example.helloworld.resources.SupplyChainResource;
import com.github.kristofa.brave.Brave;
import com.smoketurner.dropwizard.zipkin.ZipkinBundle;
import com.smoketurner.dropwizard.zipkin.ZipkinFactory;
import com.smoketurner.dropwizard.zipkin.client.ZipkinClientBuilder;
import com.smoketurner.dropwizard.zipkin.rx.BraveRxJavaSchedulersHook;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import rx.plugins.RxJavaPlugins;

public class SupplyChainApplication
        extends Application<SupplyConfiguration> {

    public static void main(String[] args) throws Exception {
        new SupplyChainApplication().run(args);
    }

    @Override
    public String getName() {
        return "Supply_Chain";
    }

    @Override
    public void initialize(Bootstrap<SupplyConfiguration> bootstrap) {
        bootstrap.addBundle(
                new ZipkinBundle<SupplyConfiguration>(getName()) {
                    @Override
                    public ZipkinFactory getZipkinFactory(
                            SupplyConfiguration configuration) {
                        return configuration.getZipkinFactory();
                    }
                });
    }

    @Override
    public void run(SupplyConfiguration configuration,
            Environment environment) throws Exception {

        final Brave brave = configuration.getZipkinFactory().build(environment);

        final Client client = new ZipkinClientBuilder(environment, brave)
                .build(configuration.getZipkinClient());

        RxJavaPlugins.getInstance()
                .registerSchedulersHook(new BraveRxJavaSchedulersHook(brave));

        // Register resources
        final SupplyChainResource resource = new SupplyChainResource(client);
        environment.jersey().register(resource);
    }
}
