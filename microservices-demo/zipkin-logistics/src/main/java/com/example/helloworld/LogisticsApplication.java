package com.example.helloworld;

import javax.ws.rs.client.Client;
import com.example.helloworld.resources.LogisticsResource;
import com.github.kristofa.brave.Brave;
import com.smoketurner.dropwizard.zipkin.ZipkinBundle;
import com.smoketurner.dropwizard.zipkin.ZipkinFactory;
import com.smoketurner.dropwizard.zipkin.client.ZipkinClientBuilder;
import com.smoketurner.dropwizard.zipkin.rx.BraveRxJavaSchedulersHook;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import rx.plugins.RxJavaPlugins;

public class LogisticsApplication
        extends Application<LogisticsConfiguration> {

    public static void main(String[] args) throws Exception {
        new LogisticsApplication().run(args);
    }

    @Override
    public String getName() {
        return "logistics";
    }

    @Override
    public void initialize(Bootstrap<LogisticsConfiguration> bootstrap) {
        bootstrap.addBundle(
                new ZipkinBundle<LogisticsConfiguration>(getName()) {
                    @Override
                    public ZipkinFactory getZipkinFactory(
                            LogisticsConfiguration configuration) {
                        return configuration.getZipkinFactory();
                    }
                });
    }

    @Override
    public void run(LogisticsConfiguration configuration,
            Environment environment) throws Exception {

        final Brave brave = configuration.getZipkinFactory().build(environment);

        final Client client = new ZipkinClientBuilder(environment, brave)
                .build(configuration.getZipkinClient());

        RxJavaPlugins.getInstance()
                .registerSchedulersHook(new BraveRxJavaSchedulersHook(brave));

        // Register resources
        final LogisticsResource resource = new LogisticsResource(client);
        environment.jersey().register(resource);
    }
}
