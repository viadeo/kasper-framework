// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.rabbitmq.client.ConnectionFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import static com.viadeo.kasper.client.platform.components.eventbus.KasperEventBusFixture.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class KasperEventBusITest {

    private static final Config DEFAULT_CONFIG = ConfigFactory.load("event-bus.conf");
    private RabbitAdmin admin;
    private KasperEventBusFactory eventBusFactory;

    @Before
    public void setup() throws Throwable {

        // Testability...
        KasperMetrics.setMetricRegistry(new MetricRegistry());

        KasperEventBusFactory factory = new KasperEventBusFactory(DEFAULT_CONFIG);

        admin = factory.rabbitAdmin(factory.connectionFactory(DEFAULT_CONFIG.getConfig("defaults.amqp")));
        admin.deleteExchange("platform");
        admin.deleteExchange("platform_dead-letter");

        for (String name : Lists.newArrayList(KasperEventBusFixture.UserEventListener.class.getName(), ChildEventListener.class.getName())) {
            admin.deleteQueue("platform_default_" + name);
            admin.deleteQueue("platform_default_" + name + "_dead-letter");
        }

    }

    @Test
    public void an_event_listener_should_receive_an_event_after_publication() throws InterruptedException {

        // Given
        EventBus eventBus = eventBus();
        Spy<UserEvent> spy = new Spy<>(1);
        UserEvent payload = new UserEvent("Chuck", "Norris", 60);
        eventBus.subscribe(new UserEventListener(spy));

        // When
        eventBus.publish(new GenericEventMessage<>(payload));

        // Then
        spy.await();
        assertEquals(1, spy.size());
        assertEquals(payload, spy.get(0));
    }


    @Test
    public void an_event_listener_should_receive_an_ordered_sequence_of_events_after_publication() throws InterruptedException {

        // Given
        EventBus eventBus = eventBus();
        Spy<UserEvent> spy = new Spy<>(3);
        UserEventListener listener = new UserEventListener(spy);

        eventBus.subscribe(listener);

        eventBus.publish(new GenericEventMessage<>(new UserEvent("Chuck", "Norris", 1)));
        eventBus.publish(new GenericEventMessage<>(new UserEvent("Chuck", "Norris", 2)));
        eventBus.publish(new GenericEventMessage<>(new UserEvent("Chuck", "Norris", 3)));

        // Then
        spy.await();
        assertEquals(3, spy.size());
        assertEquals(new Integer(1), spy.get(0).age);
        assertEquals(new Integer(2), spy.get(1).age);
        assertEquals(new Integer(3), spy.get(2).age);
    }

    @Test
    public void two_distinct_event_listeners_should_receive_the_event_after_publication() throws InterruptedException {

        // Given
        EventBus eventBus = eventBus();
        Spy<UserEvent> spy1 = new Spy<>(1);
        Spy<UserEvent> spy2 = new Spy<>(1);
        eventBus.subscribe(new UserEventListener(spy1));
        eventBus.subscribe(new ChildEventListener(spy2));

        // When
        eventBus.publish(new GenericEventMessage<>(new UserEvent("Chuck", "Norris", 1)));

        // Then
        spy1.await();
        spy2.await();
        assertEquals(1, spy1.size());
        assertEquals(1, spy2.size());
    }

    @Test
    public void the_same_listener_should_receive_the_event_only_one_time_after_publication() throws Throwable {
        // Given
        EventBus eventBus = eventBus();
        EventBus otherEventBus = eventBusFactory.create();

        Spy<UserEvent> spy = new Spy<>(1);
        eventBus.subscribe(new UserEventListener(spy));
        otherEventBus.subscribe(new UserEventListener(spy));

        // When
        eventBus.publish(new GenericEventMessage<>(new UserEvent("Chuck", "Norris", 1)));

        // Then
        spy.await();
        assertEquals(1, spy.size());
    }

    @Test
    public void bad_event_should_requeue_in_dead_letter_queue_in_order_to_avoid_infinite_loop() throws Exception {

        // Given
        EventBus eventBus = eventBus();
        Spy<UserEvent> spy = new Spy<>(1);
        eventBus.subscribe(new UserEventListener(spy));

        // When
        admin.getRabbitTemplate().send("platform", "com.viadeo.kasper.client.platform.components.eventbus.KasperEventBusFixture$UserEvent", new Message("F0".getBytes(), new MessageProperties()));
        eventBus.publish(new GenericEventMessage<>(new UserEvent("Chuck", "Norris", 1)));

        // Then
        spy.await();
        assertEquals(1, spy.size());
        Message receive = admin.getRabbitTemplate().receive("platform_default_com.viadeo.kasper.client.platform.components.eventbus.KasperEventBusFixture$UserEventListener_dead-letter");
        assertNotNull(receive);
        assertEquals("F0", new String(receive.getBody()));
    }

    @Test
    public void composite_cluster_should_publish_and_subscribe_to_both_async_and_amqp() throws InterruptedException {
        // Given
        EventBus eventBus = eventBus(DEFAULT_CONFIG.withFallback(ConfigFactory.load("event-bus-async.conf")));
        Spy<UserEvent> spy = new Spy<>(2);
        UserEvent payload = new UserEvent("Chuck", "Norris", 60);
        eventBus.subscribe(new UserEventListener(spy));

        // When
        eventBus.publish(new GenericEventMessage<>(payload));

        // Then
        spy.await();
        assertEquals(2, spy.size());
        assertEquals(payload, spy.get(0));
        assertEquals(payload, spy.get(1));
    }


    private EventBus eventBus(Config config) {

        eventBusFactory = new KasperEventBusFactory(config)
                .with(ObjectMapperProvider.INSTANCE.mapper());

        return eventBusFactory.create();
    }

    private EventBus eventBus() {
        return eventBus(DEFAULT_CONFIG);
    }
}
