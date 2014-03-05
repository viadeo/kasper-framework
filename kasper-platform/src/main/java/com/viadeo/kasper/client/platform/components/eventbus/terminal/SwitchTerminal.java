// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.terminal;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.EventBusTerminal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SwitchTerminal implements EventBusTerminal {

    private final ExecutorService executorService;
    private final EventBusTerminal primary;
    private final EventBusTerminal secondary;

    public SwitchTerminal(final EventBusTerminal primary, final EventBusTerminal secondary) {
        this.executorService = Executors.newFixedThreadPool(
                2,
                new ThreadFactoryBuilder().setNameFormat("terminal-%d").build()
        );
        this.primary = primary;
        this.secondary = secondary;
    }

    @Override
    public void publish(final EventMessage... events) {
        publish(primary, events);
        publish(secondary, events);
    }

    protected void publish(final EventBusTerminal terminal, final EventMessage... events) {
        executorService.submit(new Runnable(){
            @Override
            public void run() {
                terminal.publish(events);
            }
        });
    }

    @Override
    public void onClusterCreated(Cluster cluster) {
        primary.onClusterCreated(cluster);
    }

}
