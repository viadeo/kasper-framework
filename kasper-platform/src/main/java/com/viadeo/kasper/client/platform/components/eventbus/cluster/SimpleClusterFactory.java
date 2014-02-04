// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.cluster;

import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.SimpleCluster;

public class SimpleClusterFactory implements ClusterFactory {
    @Override
    public Cluster createCluster(String name) {
        return new SimpleCluster(name);
    }
}
