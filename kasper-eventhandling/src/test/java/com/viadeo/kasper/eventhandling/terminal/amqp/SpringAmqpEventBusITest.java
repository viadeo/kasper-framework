// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.eventhandling.terminal.amqp;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.rabbitmq.client.ConnectionFactory;
import com.typesafe.config.ConfigFactory;
import com.viadeo.kasper.event.IEvent;
import com.viadeo.kasper.eventhandling.cluster.ClassnameDynamicClusterSelector;
import com.viadeo.kasper.eventhandling.cluster.ClusterFactory;
import com.viadeo.kasper.eventhandling.cluster.fixture.SnoopEventListener;
import com.viadeo.kasper.eventhandling.cluster.fixture.groupa.GroupA;
import com.viadeo.kasper.eventhandling.cluster.fixture.groupb.GroupB;
import com.viadeo.kasper.eventhandling.serializer.JacksonSerializer;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.ClusteringEventBus;
import org.axonframework.eventhandling.EventBusTerminal;
import org.axonframework.eventhandling.SimpleCluster;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class SpringAmqpEventBusITest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringAmqpEventBusITest.class);

    private static final long TIMEOUT = 8000L;

    private static final String PREFIX = "com.viadeo.kasper.eventhandling.cluster.fixture";

    private static final Map<String, Object> SPRING_AMQP_TERMINAL_PROPERTIES = ImmutableMap.<String, Object>builder()
            // QUEUE
            .put("queue.durable", Boolean.TRUE)
            .put("queue.exclusive", Boolean.FALSE)
            .put("queue.autodelete", Boolean.FALSE)
            .put("queue.dead_letter.name_format", "%s-dead-letter")
            .put("queue.dead_letter.durable", true)
                    // EXCHANGE
            .put("exchange.name", "platform")
            .put("exchange.durable", Boolean.TRUE)
            .put("exchange.transactional", Boolean.FALSE)
            .put("exchange.dead_letter.name_format", "%s-dead-letter")
                    // CONNECTION
            .put("port", ConnectionFactory.DEFAULT_AMQP_PORT)
            .put("hostname", "127.0.0.1")
            .put("username", "kasper")
            .put("password", "kasper")
            .build();

    private static final Set<String> queues = Sets.newHashSet("groupa", "groupb");

    private ClusteringEventBus eventBus;
    private RabbitAdmin admin;
    private SpringAmqpTerminalFactory terminalFactory;

    @Before
    public void setup() throws Throwable {

        final DefaultMessageConverter messageConverter = new DefaultMessageConverter(
                new JacksonSerializer(ObjectMapperProvider.INSTANCE.mapper()),
                new ClassRoutingKeyResolver()
        );

        terminalFactory = new SpringAmqpTerminalFactory(
                ConfigFactory.parseMap(SPRING_AMQP_TERMINAL_PROPERTIES)
        ).with(messageConverter);


        eventBus = createEventBus(terminalFactory);

        admin = new RabbitAdmin(terminalFactory.connectionFactory());

        LOGGER.info("clearing...");

        for (final String queue : queues) {
            admin.deleteQueue(queue);
        }
    }

    private ClusteringEventBus createEventBus(SpringAmqpTerminalFactory terminalFactory) {

        final EventBusTerminal currentTerminal = terminalFactory.create();

        return new ClusteringEventBus(
                new ClassnameDynamicClusterSelector(PREFIX, new ClusterFactory() {
                    @Override
                    public Cluster create(final String name) {
                        return new SimpleCluster(name);
                    }
                }),
                currentTerminal
        );
    }


    @Test
    public void an_event_listener_should_receive_an_event_after_publication() throws InterruptedException {
        // Given
        final EventMessage eventMessage = new CustomEventMessage("A0");

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final SnoopEventListener delegateEventListener = new SnoopEventListener(countDownLatch);
        final GroupA.EventListenerA eventListener = spy(new GroupA.EventListenerA(delegateEventListener));

        eventBus.subscribe(eventListener);

        // When
        eventBus.publish(eventMessage);

        countDownLatch.await(TIMEOUT, TimeUnit.MILLISECONDS);

        // Then
        assertEquals("the number of received and expected message not matched,", 0, countDownLatch.getCount());

        verify(eventListener).handle(any(EventMessage.class));
        assertEquals(1, delegateEventListener.actualEvents.size());
        assertEquals("A0", delegateEventListener.actualEvents.get(0).getPayload());
    }

    @Test
    public void an_event_listener_should_receive_an_ordered_sequence_of_events_after_publication() throws InterruptedException {
        // Given
        final List<CustomEventMessage> eventMessages = Lists.newArrayList(
                new CustomEventMessage("A1"),
                new CustomEventMessage("B1"),
                new CustomEventMessage("C1")
        );

        final CountDownLatch countDownLatch = new CountDownLatch(eventMessages.size());

        final SnoopEventListener delegateEventListener = new SnoopEventListener(countDownLatch);
        final GroupA.EventListenerA eventListener = spy(new GroupA.EventListenerA(delegateEventListener));

        eventBus.subscribe(eventListener);

        // When
        for (final CustomEventMessage eventMessage : eventMessages) {
            eventBus.publish(eventMessage);
        }
        countDownLatch.await(TIMEOUT, TimeUnit.MILLISECONDS);

        // Then
        assertEquals("the number of received and expected message not matched,", 0, countDownLatch.getCount());

        verify(eventListener, times(eventMessages.size())).handle(any(EventMessage.class));

        assertEquals(3, delegateEventListener.actualEvents.size());
        assertEquals("A1", delegateEventListener.actualEvents.get(0).getPayload());
        assertEquals("B1", delegateEventListener.actualEvents.get(1).getPayload());
        assertEquals("C1", delegateEventListener.actualEvents.get(2).getPayload());
    }

    @Test
    public void two_distinct_event_listeners_should_receive_the_event_after_publication() throws InterruptedException {
        // Given
        final CountDownLatch countDownLatch = new CountDownLatch(2);

        final CustomEventMessage eventMessage = new CustomEventMessage("A2");

        final SnoopEventListener delegateEventListenerAOfGroupA = new SnoopEventListener(countDownLatch);
        final GroupA.EventListenerA eventListenerAOfGroupA = spy(new GroupA.EventListenerA(delegateEventListenerAOfGroupA));
        eventBus.subscribe(eventListenerAOfGroupA);

        final SnoopEventListener delegateEventListenerAOfGroupB = new SnoopEventListener(countDownLatch);
        final GroupB.EventListenerA eventListenerAOfGroupB = spy(new GroupB.EventListenerA(delegateEventListenerAOfGroupB));
        eventBus.subscribe(eventListenerAOfGroupB);

        // When
        eventBus.publish(eventMessage);
        countDownLatch.await(TIMEOUT, TimeUnit.MILLISECONDS);

        // Then
        assertEquals("the number of received and expected message not matched,", 0, countDownLatch.getCount());

        verify(eventListenerAOfGroupA).handle(any(EventMessage.class));
        assertEquals("A2", delegateEventListenerAOfGroupA.actualEvents.get(0).getPayload());

        verify(eventListenerAOfGroupB).handle(any(EventMessage.class));
        assertEquals("A2", delegateEventListenerAOfGroupB.actualEvents.get(0).getPayload());
    }

    @Test
    public void two_event_listeners_defined_by_the_same_domain_should_receive_the_event_only_one_time_after_publication() throws Throwable {
        // Given
        ClusteringEventBus eventBusB = createEventBus(terminalFactory);

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final SnoopEventListener delegateEventListenerA = new SnoopEventListener(countDownLatch);
        final GroupA.EventListenerA eventListenerA = new GroupA.EventListenerA(delegateEventListenerA);
        eventBus.subscribe(eventListenerA);

        final SnoopEventListener delegateEventListenerB = new SnoopEventListener(countDownLatch);
        final GroupA.EventListenerB eventListenerB = new GroupA.EventListenerB(delegateEventListenerB);
        eventBusB.subscribe(eventListenerB);

        final CustomEventMessage eventMessage = new CustomEventMessage("A4");

        // When
        eventBus.publish(eventMessage);
        countDownLatch.await(TIMEOUT, TimeUnit.MILLISECONDS);

        // Then
        assertEquals("Unexpected number of received message", 0, countDownLatch.getCount());
        assertTrue("At most one of the subscribed listeners should receive the message",
                (0 == delegateEventListenerA.actualEvents.size() && 1 == delegateEventListenerB.actualEvents.size()) ||
                        (1 == delegateEventListenerA.actualEvents.size() && 0 == delegateEventListenerB.actualEvents.size())
        );
    }

    @Test
    public void an_event_listener_should_receive_a_complex_event_after_publication() throws InterruptedException {
        // Given
        final User expectedUser = new User("Chuck", "Norris", 60);
        final GenericEventMessage<User> eventMessage = new GenericEventMessage<>(expectedUser);

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final SnoopEventListener delegateEventListener = new SnoopEventListener(countDownLatch);
        final GroupA.EventListenerA eventListener = spy(new GroupA.EventListenerA(delegateEventListener));

        eventBus.subscribe(eventListener);

        // When
        eventBus.publish(eventMessage);
        countDownLatch.await(TIMEOUT, TimeUnit.MILLISECONDS);

        // Then
        assertEquals("the number of received and expected message not matched,", 0, countDownLatch.getCount());

        verify(eventListener).handle(any(EventMessage.class));
        assertEquals(1, delegateEventListener.actualEvents.size());

        final Object payload = delegateEventListener.actualEvents.get(0).getPayload();

        assertTrue(payload instanceof User);
        assertEquals(expectedUser, payload);
    }

    @Test
    public void bad_event_should_requeue_in_dead_letter_queue_in_order_to_avoid_infinite_loop() throws Exception {
        // Given
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final SnoopEventListener delegateEventListener = new SnoopEventListener(countDownLatch);
        final GroupA.EventListenerA eventListener = spy(new GroupA.EventListenerA(delegateEventListener));

        eventBus.subscribe(eventListener);

        // When
        byte[] bytes = "F0".getBytes();
        admin.getRabbitTemplate().send("platform", "#", new Message(bytes, new MessageProperties()));
        eventBus.publish(new CustomEventMessage("F1"));
        countDownLatch.await(TIMEOUT, TimeUnit.MILLISECONDS);

        // Then
        assertEquals("the number of received and expected message not matched,", 0, countDownLatch.getCount());
        Message receive = admin.getRabbitTemplate().receive("groupa-dead-letter");
        assertNotNull(receive);
        assertEquals("F0", new String(receive.getBody()));
    }

    public static class User implements IEvent {
        public String firstName;
        public String lastName;
        public Integer age;

        public User(String firstName, String lastName, Integer age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(firstName, lastName, age);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final User other = (User) obj;
            return Objects.equal(this.firstName, other.firstName) && Objects.equal(this.lastName, other.lastName) && Objects.equal(this.age, other.age);
        }
    }

    public static class CustomEventMessage extends GenericEventMessage<String> {
        public CustomEventMessage(String payload) {
            super(payload);
        }
    }
}
