// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.rabbitmq.client.ConnectionFactory;
import com.typesafe.config.ConfigFactory;
import com.viadeo.kasper.client.platform.components.eventbus.cluster.AsynchronousClusterFactory;
import com.viadeo.kasper.client.platform.components.eventbus.cluster.DomainClusterSelectorFactory;
import com.viadeo.kasper.client.platform.components.eventbus.configuration.KasperEventBusConfiguration;
import com.viadeo.kasper.client.platform.components.eventbus.configuration.SpringAmqpTerminalConfiguration;
import com.viadeo.kasper.client.platform.components.eventbus.terminal.amqp.SpringAmqpTerminalFactory;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.IEvent;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
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

    public static final long TIMEOUT = 5000L;

    public static final SpringAmqpTerminalConfiguration AMQP_TERMINAL_CONFIGURATION = new SpringAmqpTerminalConfiguration(ConfigFactory.parseMap(
            ImmutableMap.<String, Object>builder()
                    .put("hostname", "mq01.cmurer.paris.apvo")
                    .put("port", ConnectionFactory.DEFAULT_AMQP_PORT)
                    .put("username", "integ")
                    .put("password", "integ")
                    .put("vhost", "integ")
                    .put("exchange.name", "kasper")
                    .put("exchange.type", ExchangeTypes.TOPIC)
                    .put("exchange.durable", Boolean.TRUE)
                    .put("exchange.autoDelete", Boolean.FALSE)
                    .put("queue.name", "kasper")
                    .put("queue.durable", Boolean.TRUE)
                    .put("queue.autoDelete", Boolean.FALSE)
                    .build()));

    public static final KasperEventBusConfiguration CONFIGURATION_USING_KAFKA = new KasperEventBusConfiguration(ConfigFactory.parseMap(
            ImmutableMap.<String, Object>builder()
                    .put("clusterSelector.prefix", "com.viadeo.kasper.client.platform.components")
                    .put("clusterSelector.timeUnit", "MINUTES")
                    .put("clusterSelector.pool.size", 10)
                    .put("clusterSelector.pool.maxSize", 100)
                    .put("clusterSelector.keepAliveTime", 60L)
                    .put("clusterSelector.asynchronous", Boolean.TRUE)
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

    @Rule
    public final MetricsRule metricsRule = new MetricsRule();

    @Rule
    public final EventBusRule eventBusUsingKafkaRule = new EventBusRule(CONFIGURATION_USING_KAFKA);

    public CountDownLatch countDownLatch;

    @Before
    public void setUp(){
        countDownLatch = new CountDownLatch(1);
    }

    @Test @Ignore
    public void publish_listen_usingDistributedEventBus_withAmqpConfiguration_shouldBeOk() throws InterruptedException {
        // Given
        final String expectedMessage = "Coucou David!";

        final TestEvent event = new TestEvent(expectedMessage);

        final EventListener<TestEvent> eventListener = spy(new EventListener<TestEvent>() {
            @Override
            public void handle(TestEvent event) {
                assertNotNull(event);
                assertEquals(expectedMessage, event.getMessage());
                countDownLatch.countDown();
            }
        });

        final DomainClusterSelectorFactory clusterSelectorFactory = new DomainClusterSelectorFactory(
                "com.viadeo",
                new AsynchronousClusterFactory()
        );

        final KasperEventBus eventBus = new KasperEventBusBuilder()
                .with(clusterSelectorFactory.createClusterSelector())
                .with(new SpringAmqpTerminalFactory(AMQP_TERMINAL_CONFIGURATION).createEventBusTerminal())
                .build();

        eventBus.subscribe(eventListener);

        // When
        eventBus.publish(event);
        countDownLatch.await(TIMEOUT, TimeUnit.MILLISECONDS);

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
                countDownLatch.countDown();
            }
        });

        eventBusUsingKafkaRule.subscribe(eventListener);

        // When
        eventBusUsingKafkaRule.publish(event);
        countDownLatch.await(TIMEOUT, TimeUnit.MILLISECONDS);

        // Then
        verify(eventListener).handle(any(TestEvent.class));
    }

}
