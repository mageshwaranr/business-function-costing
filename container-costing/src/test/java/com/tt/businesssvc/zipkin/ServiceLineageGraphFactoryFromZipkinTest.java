package com.tt.businesssvc.zipkin;

import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tt.businesssvc.ServiceLineageGraphFactoryFromZipkin;

import org.junit.Test;


/**
 * Created by sivarajm on 9/30/2016.
 */
public class ServiceLineageGraphFactoryFromZipkinTest
{

    @Test
    public void testFactory() {
        ServiceLineageGraphFactoryFromZipkin serviceLineageGraphFactoryFromZipkin = new ServiceLineageGraphFactoryFromZipkin();
        TinkerGraph tinkerGraph = serviceLineageGraphFactoryFromZipkin.createTinkerGraph();
        assert(tinkerGraph != null);
    }
}
