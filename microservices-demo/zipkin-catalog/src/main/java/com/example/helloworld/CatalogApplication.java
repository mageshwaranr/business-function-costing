package com.example.helloworld;

import javax.ws.rs.client.Client;
import com.example.helloworld.resources.CatalogResource;
import com.github.kristofa.brave.Brave;
import com.smoketurner.dropwizard.zipkin.ZipkinBundle;
import com.smoketurner.dropwizard.zipkin.ZipkinFactory;
import com.smoketurner.dropwizard.zipkin.client.ZipkinClientBuilder;
import com.smoketurner.dropwizard.zipkin.rx.BraveRxJavaSchedulersHook;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import rx.plugins.RxJavaPlugins;

public class CatalogApplication
        extends Application<CatalogConfiguration> {

    public static void main(String[] args) throws Exception {
        new CatalogApplication().run(args);
    }

    @Override
    public String getName() {
        return "Catalog";
    }

    @Override
    public void initialize(Bootstrap<CatalogConfiguration> bootstrap) {
        bootstrap.addBundle(
                new ZipkinBundle<CatalogConfiguration>(getName()) {
                    @Override
                    public ZipkinFactory getZipkinFactory(
                            CatalogConfiguration configuration) {
                        return configuration.getZipkinFactory();
                    }
                });
    }

    @Override
    public void run(CatalogConfiguration configuration,
            Environment environment) throws Exception {

        final Brave brave = configuration.getZipkinFactory().build(environment);

        final Client client = new ZipkinClientBuilder(environment, brave)
                .build(configuration.getZipkinClient());

        RxJavaPlugins.getInstance()
                .registerSchedulersHook(new BraveRxJavaSchedulersHook(brave));

        // Register resources
        final CatalogResource resource = new CatalogResource(client);
        environment.jersey().register(resource);
    }
}
