// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.eventhandling.terminal.amqp;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.rabbitmq.client.ConnectionFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.eventhandling.cluster.fixture.ChildEventListener;
import com.viadeo.kasper.eventhandling.cluster.fixture.Spy;
import com.viadeo.kasper.eventhandling.cluster.fixture.UserEvent;
import com.viadeo.kasper.eventhandling.cluster.fixture.UserEventListener;
import com.viadeo.kasper.eventhandling.serializer.JacksonSerializer;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SpringAmqpEventBusITest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringAmqpEventBusITest.class);

    private static final Map<String, Object> SPRING_AMQP_TERMINAL_PROPERTIES = ImmutableMap.<String, Object>builder()

            .put("clusters", Lists.<Map>newArrayList(
                    ImmutableMap.builder()
                            .put("name", "default")
                            .put("pattern", ".*")
                            .build()
            ))

            // publish
            .put("default.pub.exponentialBackOff.initialInterval", 500)
            .put("default.pub.exponentialBackOff.multiplier", 10.0)
            .put("default.pub.exponentialBackOff.maxInterval", 10000)

            // subscribe
            .put("default.sub.exchange.name", "platform")
            .put("default.sub.exchange.durable", Boolean.TRUE)
            .put("default.sub.exchange.transactional", Boolean.FALSE)
            .put("default.sub.exchange.dead_letter.name_format", "{{exchange}}_dead-letter")
            .put("default.sub.queue.durable", Boolean.TRUE)
            .put("default.sub.queue.exclusive", Boolean.FALSE)
            .put("default.sub.queue.autodelete", Boolean.FALSE)
            .put("default.sub.queue.name_format", "{{exchange}}_{{cluster}}_{{listener}}")
            .put("default.sub.queue.dead_letter.name_format", "{{queue}}_dead-letter")
            .put("default.sub.queue.dead_letter.durable", true)

             // CONNECTION
            .put("port", ConnectionFactory.DEFAULT_AMQP_PORT)
            .put("hostname", "127.0.0.1")
            .put("username", "kasper")
            .put("password", "kasper")
            .build();

    private EventBus eventBus;
    private RabbitAdmin admin;
    private EventBusFactory eventBusFactory;

    @Before
    public void setup() throws Throwable {

        // Testability...
        KasperMetrics.setMetricRegistry(new MetricRegistry());

        EventMessageConverter messageConverter = new EventMessageConverter(
                new JacksonSerializer(ObjectMapperProvider.INSTANCE.mapper())
        );

        Config config = ConfigFactory.parseMap(SPRING_AMQP_TERMINAL_PROPERTIES);
        String s = config.toString();


        eventBusFactory = new EventBusFactory(config).with(messageConverter);
        eventBus = eventBusFactory.create();
        admin = eventBusFactory.rabbitAdmin(eventBusFactory.connectionFactory(config));

        LOGGER.info("clearing...");
        admin.deleteExchange("platform");
        admin.deleteExchange("platform_dead-letter");

        for (String name : Lists.newArrayList(UserEventListener.class.getName(), ChildEventListener.class.getName())) {
            admin.deleteQueue("platform_default_" + name);
            admin.deleteQueue("platform_default_" + name + "_dead-letter");
        }
    }


    @Test
    public void an_event_listener_should_receive_an_event_after_publication() throws InterruptedException {

        // Given
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
        Spy<UserEvent> spy = new Spy<>(1);
        eventBus.subscribe(new UserEventListener(spy));

        // When
        admin.getRabbitTemplate().send("platform", "com.viadeo.kasper.eventhandling.cluster.fixture.UserEvent", new Message("F0".getBytes(), new MessageProperties()));
        eventBus.publish(new GenericEventMessage<>(new UserEvent("Chuck", "Norris", 1)));

        // Then
        spy.await();
        assertEquals(1, spy.size());
        Message receive = admin.getRabbitTemplate().receive("platform_default_com.viadeo.kasper.eventhandling.cluster.fixture.UserEventListener_dead-letter");
        assertNotNull(receive);
        assertEquals("F0", new String(receive.getBody()));
    }
}
