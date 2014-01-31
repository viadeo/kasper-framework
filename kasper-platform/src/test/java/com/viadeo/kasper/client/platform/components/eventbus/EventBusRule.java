// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.components.eventbus.configuration.KafkaTerminalConfiguration;
import com.viadeo.kasper.client.platform.components.eventbus.configuration.KasperEventBusConfiguration;
import com.viadeo.kasper.client.platform.components.eventbus.kafka.KafkaTerminal;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.IEvent;
import org.axonframework.eventhandling.ClusterSelector;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EventBusRule implements MethodRule {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventBusRule.class);

    private final KasperEventBusConfiguration eventBusConfiguration;

    private EventBusWrapper eventBusWrapper;

    public EventBusRule(final KasperEventBusConfiguration eventBusConfiguration) {
        this.eventBusConfiguration = eventBusConfiguration;
    }

    @Override
    public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                eventBusWrapper = new EventBusWrapper(createClusterSelector(), createTerminal());
                try {
                    base.evaluate();
                } finally {
                    shutdown();
                }
            }
        };
    }

    private KafkaTerminal createTerminal() {
        final KasperEventBusBuilder eventBusBuilder = new KasperEventBusBuilder();
        return (KafkaTerminal) eventBusBuilder.kafkaTerminal((KafkaTerminalConfiguration) eventBusConfiguration.getTerminalConfiguration());
    }

    private ClusterSelector createClusterSelector() {
        final KasperEventBusBuilder eventBusBuilder = new KasperEventBusBuilder();
        return eventBusBuilder.clusterSelector(eventBusConfiguration.getClusterSelectorConfiguration());
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
            this.eventBus = new KasperEventBus(clusterSelector, terminal);
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
