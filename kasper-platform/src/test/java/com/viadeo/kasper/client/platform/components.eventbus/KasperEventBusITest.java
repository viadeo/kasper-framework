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
import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.core.ExchangeTypes;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class KasperEventBusITest {

    private static final Boolean BUS_IS_ASYNC = true;

    public static CountDownLatch COUNT_DOWN_LATCH;

    public static final KasperEventBusConfiguration CONFIGURATION_USING_AMQP = new KasperEventBusConfiguration(ConfigFactory.parseMap(
            ImmutableMap.<String, Object>builder()
                    .put("clusterSelector.name", "kasper")
                    .put("clusterSelector.timeUnit", "MINUTES")
                    .put("clusterSelector.pool.size", 10)
                    .put("clusterSelector.pool.maxSize", 100)
                    .put("clusterSelector.keepAliveTime", 60L)
                    .put("clusterSelector.asynchronous", BUS_IS_ASYNC)
                    .put("terminal.amqp.hostname", "mq01.cmurer.paris.apvo")
                    .put("terminal.amqp.port", ConnectionFactory.DEFAULT_AMQP_PORT)
                    .put("terminal.amqp.username", "integ")
                    .put("terminal.amqp.password", "integ")
                    .put("terminal.amqp.vhost", "integ")
                    .put("terminal.amqp.exchange.name", "kasper")
                    .put("terminal.amqp.exchange.type", ExchangeTypes.TOPIC)
                    .put("terminal.amqp.exchange.durable", Boolean.TRUE)
                    .put("terminal.amqp.exchange.autoDelete", Boolean.FALSE)
                    .put("terminal.amqp.queue.name", "kasper")
                    .put("terminal.amqp.queue.durable", Boolean.TRUE)
                    .put("terminal.amqp.queue.autoDelete", Boolean.FALSE)
                    .build()));

    public static final KasperEventBusConfiguration CONFIGURATION_USING_KAFKA = new KasperEventBusConfiguration(ConfigFactory.parseMap(
            ImmutableMap.<String, Object>builder()
                    .put("clusterSelector.name", "kasper")
                    .put("clusterSelector.timeUnit", "MINUTES")
                    .put("clusterSelector.pool.size", 10)
                    .put("clusterSelector.pool.maxSize", 100)
                    .put("clusterSelector.keepAliveTime", 60L)
                    .put("clusterSelector.asynchronous", BUS_IS_ASYNC)
                    .put("terminal.kafka.topic", "sampleTopic")
                    .put("terminal.kafka.producer.metadata.broker.list", "localhost:9092")
                    .put("terminal.kafka.consumer.zookeeper.connect", "localhost:2181")
                    .put("terminal.kafka.consumer.zookeeper.session.timeout.ms", "400")
                    .put("terminal.kafka.consumer.zookeeper.sync.time.ms", "200")
                    .put("terminal.kafka.consumer.group.id", "0")
                    .put("terminal.kafka.consumer.auto.commit.interval.ms", "1000")
                    .build()));


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

    @Before
    public void setUp(){
        COUNT_DOWN_LATCH = new CountDownLatch(1);
    }

    @Test
    public void publish_listen_usingDistributedEventBus_withAmqpConfiguration_shouldBeOk() throws InterruptedException {
        // Given
        final String expectedMessage = "Coucou David!";

        final TestEvent event = new TestEvent(expectedMessage);

        final EventListener<TestEvent> eventListener = spy(new EventListener<TestEvent>() {
            @Override
            public void handle(TestEvent event) {
                assertNotNull(event);
                assertEquals(expectedMessage, event.getMessage());
                COUNT_DOWN_LATCH.countDown();
            }
        });

        final KasperEventBus eventBus = new KasperEventBusBuilder(CONFIGURATION_USING_AMQP).build();
        eventBus.subscribe(eventListener);

        // When
        eventBus.publish(event);
        COUNT_DOWN_LATCH.await(3000L, TimeUnit.MILLISECONDS);

        // Then
        verify(eventListener).handle(any(TestEvent.class));
    }

    @Test
    public void publish_listen_usingDistributedEventBus_withKafkaConfiguration_shouldBeOk() throws InterruptedException {
        // Given
        final String expectedMessage = "Coucou David!";

        final TestEvent event = new TestEvent(expectedMessage);

        final EventListener<TestEvent> eventListener = spy(new EventListener<TestEvent>() {
            @Override
            public void handle(TestEvent event) {
                assertNotNull(event);
                assertEquals(expectedMessage, event.getMessage());
                COUNT_DOWN_LATCH.countDown();
            }
        });

        final KasperEventBus eventBus = new KasperEventBusBuilder(CONFIGURATION_USING_KAFKA).build();
        eventBus.subscribe(eventListener);

        // When
        eventBus.publish(event);
        COUNT_DOWN_LATCH.await(3000L, TimeUnit.MILLISECONDS);

        // Then
        verify(eventListener).handle(any(TestEvent.class));
    }

}
