package com.viadeo.kasper.core.component.event.eventbus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.event.listener.EventMessage;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@Ignore(value = "waiting to be able to start/stop a backend through docker (by jocker)")
public class AMQPTopologyITest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPTopologyITest.class);

    @BeforeClass
    public static void init() throws IOException, URISyntaxException {
        // prerequisites for this suite : have a running rabbitMQ server
        RabbitMQTest.startRabbitMQ();
    }

    private final ExchangeDescriptor exchangeDescriptor = new ExchangeDescriptor("test", "0");

    @Before
    public void setUp() throws Exception {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(AMQPTopologyITest.getValidConnectionFactory());
        rabbitAdmin.deleteQueue("test-0_default1_com.viadeo.platform.component.AMQPTopologyITest$TestEventListener");
        rabbitAdmin.deleteQueue("test-0_default_com.viadeo.platform.component.AMQPTopologyITest$TestEventListener");
        rabbitAdmin.deleteQueue("test-0_default_com.viadeo.platform.component.AMQPTopologyITest$2");
        rabbitAdmin.deleteQueue("default_dead-letter");
    }

    @Test
    public void createDeadLetterQueue_isOk() {
        // Given
        AMQPTopology topology = createTopology(getValidConnectionFactory());
        topology.createExchanges(exchangeDescriptor.name, exchangeDescriptor.version);
        TestEventListener eventListener = new TestEventListener(null);

        // When
        Queue queue = topology.createDeadLetterQueue(exchangeDescriptor.name, exchangeDescriptor.version, "default", eventListener);

        // Then
        assertNotNull(queue);
        assertEquals("test-0_default_" + eventListener.getClass().getName() + "_dead-letter", queue.getName());
    }

    @Test
    public void createQueue_fromReachableRabbitMQ_isOk() {
        // Given
        AMQPTopology topology = createTopology(getValidConnectionFactory());
        topology.createExchanges(exchangeDescriptor.name, exchangeDescriptor.version);

        TestEventListener eventListener = new TestEventListener(null);

        // When
        Queue queue = topology.createQueue(exchangeDescriptor.name, exchangeDescriptor.version, "default", eventListener);

        // Then
        assertNotNull(queue);
        assertEquals("test-0_default_" + eventListener.getClass().getName(), queue.getName());
    }

    @Test(expected = AmqpConnectException.class)
    public void createQueue_fromUnreachableRabbitMQ_throwException() {
        // Given
        AMQPTopology topology = createTopology(getInvalidConnectionFactory());
        topology.createExchanges(exchangeDescriptor.name, exchangeDescriptor.version);

        TestEventListener eventListener = new TestEventListener(null);

        // When
        topology.createQueue(exchangeDescriptor.name, exchangeDescriptor.version, "default", eventListener);

        // Then throws exception
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createQueue_withRetry_fromUnstableRabbitMQ_isOk() throws IOException, URISyntaxException, InterruptedException {
        // Given
        RabbitMQTest.stopRabbitMQ();

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(2000);
        backOffPolicy.setMultiplier(2.0);
        backOffPolicy.setMaxInterval(30000);

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(backOffPolicy);

        RabbitAdmin rabbitAdmin = new RabbitAdmin(getValidConnectionFactory());
        rabbitAdmin.getRabbitTemplate().setRetryTemplate(retryTemplate);

        AMQPTopology topology = new AMQPTopology(rabbitAdmin, new ReflectionRoutingKeysResolver(), mock(QueueFinder.class));
        topology.setQueueExpires(TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS));

        TestEventListener eventListener = new TestEventListener(mock(AutowiredEventListener.class));

        // When
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

        Queue queue = topology.createQueue(exchangeDescriptor.name, exchangeDescriptor.version, "default1", eventListener);

        // Then no exception is thrown
        assertNotNull(queue);
        assertEquals("test-0_default1_" + eventListener.getClass().getName(), queue.getName());
    }

    @Test
    public void createQueue_withRabbitMQComponentInjector_withTwoDifferentEventListener_isOk() {
        // Given
        GenericApplicationContext applicationContext = new GenericApplicationContext();

        AMQPTopology topology = createTopology(getValidConnectionFactory());
        topology.addAMQPTopologyListener(new RabbitMQComponentInjector(applicationContext));

        TestEventListener eventListener1 = new TestEventListener(null);
        TestEventListener eventListener2 = new TestEventListener(null) {};

        // When
        Queue queue1 = topology.createQueue(exchangeDescriptor.name, exchangeDescriptor.version, "default", eventListener1);
        Queue queue2 = topology.createQueue(exchangeDescriptor.name, exchangeDescriptor.version, "default", eventListener2);

        // Then
        assertNotNull(queue1);
        assertEquals("test-0_default_" + eventListener1.getClass().getName(), queue1.getName());

        assertNotNull(queue2);
        assertEquals("test-0_default_" + eventListener2.getClass().getName(), queue2.getName());
    }

    static AMQPTopology createTopology(ConnectionFactory connectionFactory) {
        AMQPTopology topology = new AMQPTopology(
                new RabbitAdmin(connectionFactory),
                new ReflectionRoutingKeysResolver(),
                mock(QueueFinder.class)
        );
        topology.setQueueExpires(TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS));
        return topology;
    }

    static ConnectionFactory getValidConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        return factory;
    }

    static ConnectionFactory getInvalidConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(666);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        return factory;
    }

    public static class TestEvent implements Event {

        private final DateTime dateTime;

        public TestEvent() {
            this(DateTime.now());
        }

        @JsonCreator
        public TestEvent(@JsonProperty("dateTime") DateTime dateTime) {
            this.dateTime = dateTime.toDateTime(DateTimeZone.UTC);
        }

        public DateTime getDateTime() {
            return dateTime;
        }

        @Override
        public int hashCode() {
            return 31 * super.hashCode() + Objects.hashCode(dateTime);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final TestEvent other = (TestEvent) obj;
            return Objects.equal(this.dateTime, other.dateTime);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("dateTime", dateTime)
                    .toString();
        }
    }

    static class TestEventListener extends AutowiredEventListener<TestEvent> {

        private final EventListener<TestEvent> eventListener;

        public TestEventListener(EventListener<TestEvent> eventListener) {
            this.eventListener = eventListener;
        }

        @Override
        public EventResponse handle(EventMessage<TestEvent> message) {
            EventResponse response = eventListener.handle(message);
            synchronized (eventListener) {
                eventListener.notify();
            }
            return response;
        }
    }
}
