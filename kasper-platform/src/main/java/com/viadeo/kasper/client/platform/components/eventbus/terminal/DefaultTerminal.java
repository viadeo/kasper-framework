// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.terminal;

import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.EventBusTerminal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultTerminal implements EventBusTerminal {

    private final List<Cluster> clusters = new CopyOnWriteArrayList<>();

    @Override
    public void publish(final EventMessage... events) {
        for (Cluster cluster : clusters) {
            cluster.publish(events);
        }
    }

    @Override
    public void onClusterCreated(final Cluster cluster) {
        clusters.add(cluster);
    }
}
