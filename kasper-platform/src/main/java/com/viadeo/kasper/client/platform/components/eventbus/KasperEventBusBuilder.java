// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.components.eventbus.terminal.DefaultTerminal;
import org.axonframework.eventhandling.ClusterSelector;
import org.axonframework.eventhandling.DefaultClusterSelector;
import org.axonframework.eventhandling.EventBusTerminal;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperEventBusBuilder {

    private final List<PublicationListener> publicationListeners;
    private ClusterSelector clusterSelector;
    private EventBusTerminal eventBusTerminal;
    private KasperProcessorDownLatch processorDownLatch;

    public KasperEventBusBuilder() {
        this.publicationListeners = Lists.newArrayList();
    }

    public KasperEventBusBuilder with(final ClusterSelector clusterSelector) {
        this.clusterSelector = clusterSelector;
        return this;
    }

    public KasperEventBusBuilder with(final EventBusTerminal eventBusTerminal) {
        this.eventBusTerminal = eventBusTerminal;
        return this;
    }

    public KasperEventBusBuilder with(final KasperProcessorDownLatch processorDownLatch) {
        this.processorDownLatch = processorDownLatch;
        return this;
    }

    public KasperEventBusBuilder with(final PublicationListener... publicationListeners) {
        checkNotNull(publicationListeners);
        this.publicationListeners.addAll(Lists.newArrayList(publicationListeners));
        return this;
    }

    public KasperEventBus build() {
        if (null == clusterSelector) {
            clusterSelector = new DefaultClusterSelector();
        }

        if (null == eventBusTerminal) {
            eventBusTerminal = new DefaultTerminal();
        }

        final KasperEventBus eventBus = new KasperEventBus(
                clusterSelector,
                eventBusTerminal,
                Optional.fromNullable(processorDownLatch)
        );

        for (final PublicationListener publicationListener:publicationListeners) {
            eventBus.addPublicationListener(publicationListener);
        }

        return eventBus;
    }
}