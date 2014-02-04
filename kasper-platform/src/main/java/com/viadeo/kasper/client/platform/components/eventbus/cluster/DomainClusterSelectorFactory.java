// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.cluster;

import com.google.common.base.Function;
import org.axonframework.eventhandling.Cluster;

public class DomainClusterSelectorFactory implements ClusterSelectorFactory {

    private final String prefix;
    private final ClusterFactory clusterFactory;

    public DomainClusterSelectorFactory(final String prefix, final ClusterFactory clusterFactory) {
        this.prefix = prefix;
        this.clusterFactory = clusterFactory;
    }

    public DomainClusterSelector createClusterSelector() {
        return new DomainClusterSelector(
                prefix,
                new Function<String, Cluster>() {
                    @Override
                    public Cluster apply(final String name) {
                        return clusterFactory.createCluster(name);
                    }
                }
        );
    }
}
