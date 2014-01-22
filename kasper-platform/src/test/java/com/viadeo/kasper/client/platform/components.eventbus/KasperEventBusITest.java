package com.viadeo.kasper.client.platform.components.eventbus;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.rabbitmq.client.ConnectionFactory;
import com.typesafe.config.ConfigFactory;
import com.viadeo.kasper.client.platform.components.eventbus.configuration.KasperEventBusConfiguration;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.IEvent;
import org.junit.Test;
import org.springframework.amqp.core.ExchangeTypes;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class KasperEventBusITest {

    private static final Boolean BUS_IS_ASYNC = true;

    public static class TestEvent implements IEvent {
        private static final long serialVersionUID = 7330469676458292421L;

        private final String message;

        @JsonCreator
        public TestEvent(@JsonProperty("message") String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public KasperEventBusITest() {
        KasperMetrics.setMetricRegistry(new MetricRegistry());
    }

    @Test
    public void publish_withDistributedTerminal_shouldBeOk() throws InterruptedException {
        // Given
        final KasperEventBusConfiguration configuration = new KasperEventBusConfiguration(ConfigFactory.parseMap(
                ImmutableMap.<String, Object>builder()
                        .put("clusterSelector.name", "kasper")
                        .put("clusterSelector.timeUnit", "MINUTES")
                        .put("clusterSelector.pool.size", 10)
                        .put("clusterSelector.pool.maxSize", 100)
                        .put("clusterSelector.keepAliveTime", 60L)
                        .put("clusterSelector.asynchronous", BUS_IS_ASYNC)
                        .put("terminal.hostname", "mq01.cmurer.paris.apvo")
                        .put("terminal.port", ConnectionFactory.DEFAULT_AMQP_PORT)
                        .put("terminal.username", "integ")
                        .put("terminal.password", "integ")
                        .put("terminal.vhost", "integ")
                        .put("terminal.exchange.name", "kasper")
                        .put("terminal.exchange.type", ExchangeTypes.TOPIC)
                        .put("terminal.exchange.durable", Boolean.TRUE)
                        .put("terminal.exchange.autoDelete", Boolean.FALSE)
                        .put("terminal.queue.name", "kasper")
                        .put("terminal.queue.durable", Boolean.TRUE)
                        .put("terminal.queue.autoDelete", Boolean.FALSE)
                        .build()));

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final String expectedMessage = "Coucou David!";

        final EventListener<TestEvent> eventListener = spy(new EventListener<TestEvent>() {
            @Override
            public void handle(TestEvent event) {
                assertNotNull(event);
                assertEquals(expectedMessage, event.getMessage());
                countDownLatch.countDown();
            }
        });

        final KasperEventBus eventBus = new KasperEventBusFactory(configuration).build();
        eventBus.subscribe(eventListener);

        final TestEvent event = new TestEvent(expectedMessage);

        // When
        eventBus.publish(event);

        // Then
        countDownLatch.await(3000L, TimeUnit.MILLISECONDS);
        verify(eventListener).handle(any(TestEvent.class));
    }

}
