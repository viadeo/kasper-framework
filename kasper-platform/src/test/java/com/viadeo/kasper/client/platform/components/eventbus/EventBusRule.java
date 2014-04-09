// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.components.eventbus.cluster.AsynchronousClusterFactory;
import com.viadeo.kasper.client.platform.components.eventbus.cluster.ClusterSelectorFactory;
import com.viadeo.kasper.client.platform.components.eventbus.cluster.DomainClusterSelectorFactory;
import com.viadeo.kasper.client.platform.components.eventbus.configuration.KafkaTerminalConfiguration;
import com.viadeo.kasper.client.platform.components.eventbus.configuration.KasperEventBusConfiguration;
import com.viadeo.kasper.client.platform.components.eventbus.terminal.kafka.KafkaTerminal;
import com.viadeo.kasper.client.platform.components.eventbus.terminal.kafka.KafkaTerminalFactory;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.IEvent;
import kafka.consumer.ConsumerConfig;
import kafka.producer.ProducerConfig;
import org.axonframework.eventhandling.ClusterSelector;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EventBusRule implements MethodRule {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventBusRule.class);

    private final KafkaTerminalFactory terminalFactory;
    private final ClusterSelectorFactory clusterSelectorFactory;

    private EventBusWrapper eventBusWrapper;

    public EventBusRule(final KasperEventBusConfiguration eventBusConfiguration){
        this(
                new KafkaTerminalFactory(
                        new ConsumerConfig(((KafkaTerminalConfiguration)eventBusConfiguration.getTerminalConfiguration()).getConsumerConfiguration().toProperties()),
                        new ProducerConfig(((KafkaTerminalConfiguration)eventBusConfiguration.getTerminalConfiguration()).getProducerConfiguration().toProperties())
                ),
                new DomainClusterSelectorFactory(
                        eventBusConfiguration.getClusterSelectorConfiguration().getPrefix(),
                        new AsynchronousClusterFactory()
                )
        );
    }

    public EventBusRule(
            final KafkaTerminalFactory terminalFactory,
            final ClusterSelectorFactory clusterSelectorFactory
    ) {
        this.clusterSelectorFactory = clusterSelectorFactory;
        this.terminalFactory = terminalFactory;
    }

    @Override
    public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                eventBusWrapper = new EventBusWrapper(
                        clusterSelectorFactory.createClusterSelector(),
                        terminalFactory.createEventBusTerminal()
                );
                try {
                    base.evaluate();
                } finally {
                    shutdown();
                }
            }
        };
    }

    private void shutdown() {
        LOGGER.info("shutdowning event bus...");
        eventBusWrapper.shutdown();
    }

    public void publish(final IEvent event) {
        LOGGER.info("publishing event bus...");
        eventBusWrapper.publish(event);
    }

    public void subscribe(final EventListener... eventListeners) {
        eventBusWrapper.subscribe(eventListeners);
    }

    public static class EventBusWrapper {

        private final List<EventListener> eventListeners;
        private final KasperEventBus eventBus;
        private final KafkaTerminal terminal;

        public EventBusWrapper(final ClusterSelector clusterSelector, final KafkaTerminal terminal) {
            this.terminal = terminal;
            this.eventBus = new KasperEventBusBuilder().with(clusterSelector).with(terminal).build();
            this.eventListeners = Lists.newArrayList();
        }

        public void publish(final IEvent event) {
            eventBus.publish(event);
        }

        public void subscribe(final EventListener... eventListeners) {
            for (EventListener eventListener : eventListeners) {
                eventBus.subscribe(eventListener);
                this.eventListeners.add(eventListener);
            }
        }

        public void unsubscribeAll() {
            for (EventListener eventListener : eventListeners) {
                eventBus.unsubscribe(eventListener);
            }
            eventListeners.clear();
        }

        public void shutdown() {
            unsubscribeAll();
            terminal.shutdown();
        }
    }
}
