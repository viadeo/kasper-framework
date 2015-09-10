package com.viadeo.kasper.core.component.event.eventbus;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.ContextHelper;
import com.viadeo.kasper.api.id.IDBuilder;
import com.viadeo.kasper.api.id.SimpleIDBuilder;
import com.viadeo.kasper.common.serde.ObjectMapperProvider;
import com.viadeo.kasper.core.component.event.eventbus.spring.RabbitMQConfiguration;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import com.viadeo.kasper.core.context.DefaultContextHelper;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.axonframework.domain.GenericEventMessage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;

import static com.viadeo.kasper.core.component.event.eventbus.spring.RabbitMQEventBusConfiguration.AmqpClusterConfiguration;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@Ignore(value = "waiting to be able to start/stop a backend through docker (by jocker)")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AMQPClusterITest.TestConfiguration.class, RabbitMQConfiguration.class, AmqpClusterConfiguration.class})
@ActiveProfiles(profiles = "rabbitmq")
public class AMQPClusterITest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPClusterITest.class);

    public static MetricRegistry METRIC_REGISTRY = new MetricRegistry();

    public static Config CONFIG = ConfigFactory.parseMap(
            ImmutableMap.<String, Object>builder()
                    .put("runtime.eventbus.amqp.clusterName", "default")
                    .put("runtime.eventbus.amqp.enableListeners", true)
                    .put("runtime.eventbus.amqp.corePoolSize", 50)
                    .put("runtime.eventbus.amqp.maximumPoolSize", 500)
                    .put("runtime.eventbus.amqp.prefetchCount", 10)

                    .put("runtime.eventbus.amqp.retry.exponentialBackOff.initialInterval", 2000)
                    .put("runtime.eventbus.amqp.retry.exponentialBackOff.multiplier", 2.0)
                    .put("runtime.eventbus.amqp.retry.exponentialBackOff.maxInterval", 30000)

                    .put("runtime.eventbus.amqp.exchange.name", "framework")
                    .put("runtime.eventbus.amqp.exchange.version", "1")
                    .put("runtime.eventbus.amqp.exchange.durable", true)
                    .put("runtime.eventbus.amqp.exchange.transactional", false)
                    .put("runtime.eventbus.amqp.exchange.deadLetterNameFormat", "%exchange%_dead-letter")

                    .put("runtime.eventbus.amqp.queue.durable", true)
                    .put("runtime.eventbus.amqp.queue.exclusive", false)
                    .put("runtime.eventbus.amqp.queue.autodelete", false)
                    .put("runtime.eventbus.amqp.queue.expires", "7 days")
                    .put("runtime.eventbus.amqp.queue.messageTTL", "7 days")
                    .put("runtime.eventbus.amqp.queue.nameFormat", "%exchange%_%cluster%_%listener%")
                    .put("runtime.eventbus.amqp.queue.deadLetterNameFormat", "%queue%_dead-letter")
                    .put("runtime.eventbus.amqp.queue.deadLetterMaxLength", 100)

                    .put("infrastructure.rabbitmq.addresses", Lists.newArrayList("localhost:5672"))
                    .put("infrastructure.rabbitmq.username", "guest")
                    .put("infrastructure.rabbitmq.password", "guest")
                    .put("infrastructure.rabbitmq.virtualhost", "/")
                    .put("infrastructure.rabbitmq.port", "5672")
                    .put("infrastructure.rabbitmq.hosts", "localhost")

                    .put("infrastructure.rabbitmq.mgmt.hostname", "localhost")
                    .put("infrastructure.rabbitmq.mgmt.port", 15672)

                    .put("runtime.eventbus.amqp.interceptor.retry.maxAttempts", 5)

                    .build()
    );

    private static AMQPTopologyITest.TestEventListener eventListenerWrapper;
    private static final AutowiredEventListener<AMQPTopologyITest.TestEvent> mockedEventListener = mock(AutowiredEventListener.class);

    @BeforeClass
    public static void init() throws Exception {
        KasperMetrics.setMetricRegistry(METRIC_REGISTRY);
    }

    @Inject
    private AMQPCluster cluster;

    @Inject
    private ConnectionFactory connectionFactory;

    @Inject
    private Config config;

    @Before
    public void setUp() throws Exception {
        RabbitMQTest.startRabbitMQ();

        if (eventListenerWrapper == null) {
            clearQueues(connectionFactory);

            eventListenerWrapper = new AMQPTopologyITest.TestEventListener(mockedEventListener);

            cluster.subscribe(eventListenerWrapper);
        } else {

            reset(mockedEventListener);
        }
    }

    @Test
    public void doPublish_fromReachableRabbitMQ_isOk() throws InterruptedException {
        // Given
        ArgumentCaptor<AMQPTopologyITest.TestEvent> eventCaptor = ArgumentCaptor.forClass(AMQPTopologyITest.TestEvent.class);
        AMQPTopologyITest.TestEvent testEvent = new AMQPTopologyITest.TestEvent();

        // When
        cluster.doPublish(new GenericEventMessage<>(testEvent));

        synchronized (mockedEventListener) {
            mockedEventListener.wait(1000);
        }

        // Then
        verify(mockedEventListener).handle(any(Context.class), eventCaptor.capture());
        assertEquals(1, eventCaptor.getAllValues().size());
        assertEquals(testEvent, eventCaptor.getValue());
    }

    @Test(expected = AmqpException.class)
    public void doPublish_fromUnreachableRabbitMQ_ThrowException() throws InterruptedException, IOException, URISyntaxException {
        // Given
        RabbitMQTest.stopRabbitMQ();

        // When
        cluster.doPublish(new GenericEventMessage<>(new AMQPTopologyITest.TestEvent()));

        // Then throws exception
    }

    @Test
    public void doPublish_fromUnstableRabbitMQ_isOk() throws InterruptedException, IOException, URISyntaxException {
        // Given
        ArgumentCaptor<AMQPTopologyITest.TestEvent> eventCaptor = ArgumentCaptor.forClass(AMQPTopologyITest.TestEvent.class);
        AMQPTopologyITest.TestEvent testEvent = new AMQPTopologyITest.TestEvent();

        RabbitMQTest.stopRabbitMQ();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    RabbitMQTest.startRabbitMQ();
                } catch (InterruptedException | IOException | URISyntaxException e) {
                    LOGGER.error("failed to turn on the rabbitmq-server", e);
                }
            }
        }).start();

        cluster.doPublish(new GenericEventMessage<>(testEvent));

        synchronized (mockedEventListener) {
            mockedEventListener.wait(5000);
        }

        // Then
        verify(mockedEventListener).handle(any(Context.class), eventCaptor.capture());
        assertEquals(1, eventCaptor.getAllValues().size());
        assertEquals(testEvent, eventCaptor.getValue());
    }

    @Test
    public void doPublish_fromCrashedRabbitMQ_isOk() throws InterruptedException, IOException, URISyntaxException {
        // Given
        ArgumentCaptor<AMQPTopologyITest.TestEvent> eventCaptor = ArgumentCaptor.forClass(AMQPTopologyITest.TestEvent.class);
        AMQPTopologyITest.TestEvent testEvent = new AMQPTopologyITest.TestEvent();

        clearQueues(connectionFactory);
        RabbitMQTest.stopRabbitMQ();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    RabbitMQTest.startRabbitMQ();
                } catch (InterruptedException | IOException | URISyntaxException e) {
                    LOGGER.error("failed to turn on the rabbitmq-server", e);
                }
            }
        }).start();

        cluster.doPublish(new GenericEventMessage<>(testEvent));

        synchronized (mockedEventListener) {
            mockedEventListener.wait(5000);
        }

        // Then
        try {
            verify(mockedEventListener).handle(any(Context.class), eventCaptor.capture());
            assertEquals(1, eventCaptor.getAllValues().size());
            assertEquals(testEvent, eventCaptor.getValue());
        } finally {
            // restore queues!!!
            new AmqpClusterConfiguration()
                    .amqpTopology(config, new RabbitAdmin(connectionFactory), null, new AMQPComponentNameFormatter())
                    .createQueue(
                            config.getString("runtime.eventbus.amqp.exchange.name"),
                            config.getString("runtime.eventbus.amqp.exchange.version"),
                            config.getString("runtime.eventbus.amqp.clusterName"),
                            eventListenerWrapper
                    );
        }

    }

    private static void clearQueues(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.deleteQueue("test-0_default_com.viadeo.kasper.core.component.eventbus.AMQPTopologyITest$TestEventListener");
        rabbitAdmin.deleteQueue("default_dead-letter");
    }

    @Configuration
    public static class TestConfiguration {
        @Bean
        public Config config() {
            return CONFIG;
        }

        @Bean
        public MetricRegistry metricRegistry() {
            return METRIC_REGISTRY;
        }

        @Bean
        public ObjectMapper objectMapper() {
            return ObjectMapperProvider.INSTANCE.mapper();
        }

        @Bean
        public IDBuilder idBuilder() {
            return new SimpleIDBuilder(TestFormats.DB_ID,
                    TestFormats.UUID);
        }

        @Bean
        public ContextHelper contextHelper(IDBuilder idBuilder) {
            return new DefaultContextHelper(idBuilder);
        }
    }
}