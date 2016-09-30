package com.example.helloworld;

import javax.ws.rs.client.Client;
import com.example.helloworld.resources.CustomerResource;
import com.github.kristofa.brave.Brave;
import com.smoketurner.dropwizard.zipkin.ZipkinBundle;
import com.smoketurner.dropwizard.zipkin.ZipkinFactory;
import com.smoketurner.dropwizard.zipkin.client.ZipkinClientBuilder;
import com.smoketurner.dropwizard.zipkin.rx.BraveRxJavaSchedulersHook;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import rx.plugins.RxJavaPlugins;

public class CustomerApplication
        extends Application<CustomerConfiguration> {

    public static void main(String[] args) throws Exception {
        new CustomerApplication().run(args);
    }

    @Override
    public String getName() {
        return "Customer";
    }

    @Override
    public void initialize(Bootstrap<CustomerConfiguration> bootstrap) {
        bootstrap.addBundle(
                new ZipkinBundle<CustomerConfiguration>(getName()) {
                    @Override
                    public ZipkinFactory getZipkinFactory(
                            CustomerConfiguration configuration) {
                        return configuration.getZipkinFactory();
                    }
                });
    }

    @Override
    public void run(CustomerConfiguration configuration,
            Environment environment) throws Exception {

        final Brave brave = configuration.getZipkinFactory().build(environment);

        final Client client = new ZipkinClientBuilder(environment, brave)
                .build(configuration.getZipkinClient());

        RxJavaPlugins.getInstance()
                .registerSchedulersHook(new BraveRxJavaSchedulersHook(brave));

        // Register resources
        final CustomerResource resource = new CustomerResource(client);
        environment.jersey().register(resource);
    }
}
