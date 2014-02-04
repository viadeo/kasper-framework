// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.ClusterSelector;
import org.axonframework.eventhandling.DefaultClusterSelector;
import org.axonframework.eventhandling.EventBusTerminal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class KasperEventBusBuilder {

    private ClusterSelector clusterSelector;
    private EventBusTerminal eventBusTerminal;

    public KasperEventBusBuilder with(ClusterSelector clusterSelector) {
        this.clusterSelector = clusterSelector;
        return this;
    }

    public KasperEventBusBuilder with(EventBusTerminal eventBusTerminal) {
        this.eventBusTerminal = eventBusTerminal;
        return this;
    }

    public KasperEventBus build() {
        if (null == clusterSelector) {
            clusterSelector = new DefaultClusterSelector();
        }

        if (null == eventBusTerminal) {
            eventBusTerminal = new DefaultEventBusTerminal();
        }

        return new KasperEventBus(clusterSelector, eventBusTerminal);
    }

    private static class DefaultEventBusTerminal implements EventBusTerminal {

        private List<Cluster> clusters = new CopyOnWriteArrayList<>();

        @Override
        public void publish(EventMessage... events) {
            for (Cluster cluster : clusters) {
                cluster.publish(events);
            }
        }

        @Override
        public void onClusterCreated(Cluster cluster) {
            clusters.add(cluster);
        }
    }
}
