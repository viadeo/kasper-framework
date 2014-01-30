// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.terminal;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.EventBusTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkNotNull;

public class SwitchTerminal implements EventBusTerminal {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwitchTerminal.class);

    private final EventBusTerminal primary;
    private final ExecutorService primaryExecutor;

    private final EventBusTerminal secondary;
    private final ExecutorService secondaryExecutor;

    public SwitchTerminal(final EventBusTerminal primary, final EventBusTerminal secondary) {
        this.primary = checkNotNull(primary);
        this.secondary = checkNotNull(secondary);
        this.primaryExecutor = Executors.newFixedThreadPool(
                2, new ThreadFactoryBuilder().setNameFormat("primary-terminal-%d").build()
        );
        this.secondaryExecutor = Executors.newFixedThreadPool(
                2, new ThreadFactoryBuilder().setNameFormat("secondary-terminal-%d").build()
        );
    }

    @Override
    public void publish(final EventMessage... events) {
        if (null != events) {
            publish(primaryExecutor, primary, events);
            publish(secondaryExecutor, secondary, events);
        } else {
            // here we instantiate an exception in order to identify the call origin
            LOGGER.warn("Skipped publishing : no defined events", new RuntimeException());
        }
    }

    private void publish(
            final ExecutorService executor,
            final EventBusTerminal terminal,
            final EventMessage... events
    ) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    terminal.publish(events);
                } catch (Throwable t) {
                    LOGGER.error("Unexpected error during publishing messages : {}", Lists.newArrayList(events), t);
                }
            }
        });
    }

    @Override
    public void onClusterCreated(final Cluster cluster) {
        primary.onClusterCreated(cluster);
    }

}
