// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.eventbus;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import com.viadeo.kasper.core.component.event.eventbus.spring.RabbitMQConfiguration;
import com.viadeo.kasper.core.component.event.eventbus.spring.RabbitMQEventBusConfiguration;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.axonframework.domain.GenericEventMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static com.viadeo.kasper.core.component.event.eventbus.KasperEventBusFixture.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        AMQPClusterITest.TestConfiguration.class,
        RabbitMQConfiguration.class
})
@ActiveProfiles(profiles = "rabbitmq")
public class KasperEventBusITest {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperEventBusITest.class);

    private static TestEventBus eventBus;

    @Inject
    private ApplicationContext context;

    @Inject
    private RabbitAdmin admin;

    @Inject
    private Config config;

    private AMQPComponentNameFormatter formatter;

    private String exchangeName;
    private String versionedExchangeName;
    private String clusterName;

    @Before
    public void setup() throws Throwable {
        KasperMetrics.setMetricRegistry(new MetricRegistry());

        formatter = new RabbitMQEventBusConfiguration.AmqpClusterConfiguration().amqpComponentNameFormatter();

        ExchangeDescriptor exchangeDescriptor = new ExchangeDescriptor(
                config.getString("runtime.eventbus.amqp.exchange.name"),
                config.getString("runtime.eventbus.amqp.exchange.version")
        );

        exchangeName = exchangeDescriptor.name;

        versionedExchangeName = formatter.getFullExchangeName(
                exchangeDescriptor.name,
                exchangeDescriptor.version
        );

        clusterName = config.getString("runtime.eventbus.amqp.clusterName");

        try {
            String deadLetterExchangeName = formatter.getDeadLetterExchangeName(exchangeDescriptor.name, exchangeDescriptor.version);

            admin.deleteExchange(versionedExchangeName);
            admin.deleteExchange(deadLetterExchangeName);

            for (String name : Lists.newArrayList(UserEventListener.class.getName(), ChildEventListener.class.getName())) {
                admin.deleteQueue(versionedExchangeName + "_" + clusterName + "_" + name);
                admin.deleteQueue(versionedExchangeName + "_" + clusterName + "_" + name + "_dead-letter");
            }
        } catch (RuntimeException e) {
            LOGGER.error("Failed to tear down...", e);
        }
    }

    @Test
    public void an_event_listener_should_receive_an_event_after_publication() throws InterruptedException {
        // Given
        Spy<UserEvent> spy = new Spy<>(1);
        UserEventListener listener = new UserEventListener(spy);

        TestEventBus eventBus = eventBus();
        eventBus.subscribe(listener);
        eventBus.startCluster();

        UserEvent payload = new UserEvent("Chuck", "Norris", 60);

        // When
        eventBus.publish(new GenericEventMessage<>(payload));

        // Then
        spy.await();
        assertEquals(1, spy.size());
        assertEquals(payload, spy.get(0));

        eventBus.unsubscribe(listener);
    }

    @Test
    public void an_event_listener_should_receive_an_ordered_sequence_of_events_after_publication() throws InterruptedException {
        // Given
        Spy<UserEvent> spy = new Spy<>(3);
        UserEventListener listener = new UserEventListener(spy);

        TestEventBus eventBus = eventBus();
        eventBus.subscribe(listener);
        eventBus.startCluster();

        eventBus.publish(new GenericEventMessage<>(new UserEvent("Chuck", "Norris", 1)));
        eventBus.publish(new GenericEventMessage<>(new UserEvent("Chuck", "Norris", 2)));
        eventBus.publish(new GenericEventMessage<>(new UserEvent("Chuck", "Norris", 3)));

        // Then
        spy.await();
        assertEquals(3, spy.size());
        assertEquals(new Integer(1), spy.get(0).age);
        assertEquals(new Integer(2), spy.get(1).age);
        assertEquals(new Integer(3), spy.get(2).age);

        eventBus.unsubscribe(listener);
    }

    @Test
    public void two_distinct_event_listeners_should_receive_the_event_after_publication() throws InterruptedException {
        // Given
        Spy<UserEvent> spy1 = new Spy<>(1);
        UserEventListener listener1 = new UserEventListener(spy1);

        Spy<UserEvent> spy2 = new Spy<>(1);
        ChildEventListener listener2 = new ChildEventListener(spy2);

        TestEventBus eventBus = eventBus();
        eventBus.subscribe(listener1);
        eventBus.subscribe(listener2);
        eventBus.startCluster();

        // When
        eventBus.publish(new GenericEventMessage<>(new UserEvent("Chuck", "Norris", 1)));

        // Then
        spy1.await();
        spy2.await();
        assertEquals(1, spy1.size());
        assertEquals(1, spy2.size());

        eventBus.unsubscribe(listener1);
        eventBus.unsubscribe(listener2);
    }

    @Test
    public void the_same_listener_should_receive_the_event_only_one_time_after_publication() throws Throwable {
        // Given
        Spy<UserEvent> spy = new Spy<>(1);
        UserEventListener listener = new UserEventListener(spy);

        TestEventBus eventBus = eventBus();
        eventBus.subscribe(listener);
        eventBus.startCluster();

        TestEventBus otherEventBus = newEventBus();
        otherEventBus.subscribe(listener);
        otherEventBus.startCluster();

        // When
        eventBus.publish(new GenericEventMessage<>(new UserEvent("Chuck", "Norris", 1)));

        // Then
        spy.await();
        assertEquals(1, spy.size());

        eventBus.unsubscribe(listener);
        otherEventBus.unsubscribe(listener);
    }

    @Test
    public void bad_event_should_requeue_in_dead_letter_queue_in_order_to_avoid_infinite_loop() throws Exception {
        // Given
        Spy<UserEvent> spy = new Spy<>(1);
        UserEventListener listener = new UserEventListener(spy);

        TestEventBus eventBus = eventBus();
        eventBus.subscribe(listener);
        eventBus.startCluster();

        String deadLetterQueueName = formatter.getDeadLetterQueueName(versionedExchangeName, clusterName, listener);

        // When
        admin.getRabbitTemplate().send(
                exchangeName,
                "com.viadeo.kasper.core.component.event.eventbus.KasperEventBusFixture$UserEvent",
                new Message("F0".getBytes(), new MessageProperties())
        );
        eventBus.publish(new GenericEventMessage<>(new UserEvent("Chuck", "Norris", 1)));

        // Then
        spy.await();
        assertEquals(1, spy.size());

        Message receive = admin.getRabbitTemplate().receive(deadLetterQueueName);
        assertNotNull(receive);
        assertEquals("F0", new String(receive.getBody()));

        eventBus.unsubscribe(listener);
    }

    private TestEventBus eventBus() {
        if (eventBus == null) {
            eventBus = newEventBus();
        }
        return eventBus;
    }

    private TestEventBus newEventBus() {
        return new TestEventBus(amqpCluster());
    }

    class TestEventBus extends KasperEventBus {

        private AMQPCluster cluster;

        public TestEventBus(AMQPCluster cluster) {
            super(new MetricRegistry(), cluster);
            this.cluster = cluster;
        }

        public void startCluster() {
            this.cluster.start();
        }
    }

    AMQPCluster amqpCluster() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles(KasperEventBusITest.class.getAnnotation(ActiveProfiles.class).profiles());
        context.setParent(this.context);
        context.register(
                RabbitMQEventBusConfiguration.class,
                RabbitMQEventBusConfiguration.AmqpClusterConfiguration.class
        );
        context.refresh();

        return context.getBean(AMQPCluster.class);
    }
}
