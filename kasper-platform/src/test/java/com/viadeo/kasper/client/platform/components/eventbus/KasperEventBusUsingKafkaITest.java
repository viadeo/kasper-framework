// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Queues;
import com.typesafe.config.ConfigFactory;
import com.viadeo.kasper.client.platform.components.eventbus.configuration.KasperEventBusConfiguration;
import com.viadeo.kasper.client.platform.components.eventbus.fixture.domainA.DomainA;
import com.viadeo.kasper.client.platform.components.eventbus.fixture.domainA.EventB;
import com.viadeo.kasper.client.platform.components.eventbus.fixture.domainB.DomainB;
import com.viadeo.kasper.event.EventMessage;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class KasperEventBusUsingKafkaITest {

    public static final KasperEventBusConfiguration CONFIGURATION_USING_KAFKA = new KasperEventBusConfiguration(ConfigFactory.parseMap(
            ImmutableMap.<String, Object>builder()
                    .put("clusterSelector.prefix", "com.viadeo.kasper.client.platform.components.eventbus.fixture")
                    .put("clusterSelector.timeUnit", "MINUTES")
                    .put("clusterSelector.pool.size", 10)
                    .put("clusterSelector.pool.maxSize", 100)
                    .put("clusterSelector.keepAliveTime", 60L)
                    .put("clusterSelector.asynchronous", false)
                    .put("terminal.kafka.producer.metadata.broker.list", "localhost:9092")
                    .put("terminal.kafka.producer.producer.type", "sync")
                    .put("terminal.kafka.consumer.zookeeper.connect", "localhost:2181")
                    .put("terminal.kafka.consumer.zookeeper.session.timeout.ms", "400")
                    .put("terminal.kafka.consumer.zookeeper.sync.time.ms", "200")
                    .put("terminal.kafka.consumer.group.id", "0")
                    .put("terminal.kafka.consumer.auto.commit.interval.ms", "1000")
                    .build()));

    private static final long TIMEOUT = 2000L;

    @Rule
    public final MetricsRule metricsRule = new MetricsRule();

    @Rule
    public final EventBusRule eventBusRuleA = new EventBusRule(CONFIGURATION_USING_KAFKA);

    @Rule
    public final EventBusRule eventBusRuleB = new EventBusRule(CONFIGURATION_USING_KAFKA);

    @Test
    public void an_event_listener_should_receive_an_event_after_publication() throws InterruptedException {
        // Given
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final DomainA.EventC event = new DomainA.EventC();

        final DomainA.EventListenerC eventListenerCOfDomainA = prepareEventListenerCOfDomainA(countDownLatch, event);

        eventBusRuleA.subscribe(eventListenerCOfDomainA);

        // When
        eventBusRuleA.publish(event);
        countDownLatch.await(TIMEOUT, TimeUnit.MILLISECONDS);

        // Then
        verify(eventListenerCOfDomainA).handle(any(DomainA.EventC.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void an_event_listener_should_not_receive_an_event_that_is_not_dedicated_to_itself_after_publication() throws InterruptedException {
        // Given
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final DomainB.EventListenerB eventListenerBOfDomainB = spy(new DomainB.EventListenerB());

        eventBusRuleA.subscribe(eventListenerBOfDomainB);

        // When
        eventBusRuleA.publish(new DomainB.EventC());
        countDownLatch.await(TIMEOUT, TimeUnit.MILLISECONDS);

        // Then
        verify(eventListenerBOfDomainB, never()).handle(any(EventMessage.class));
    }

    @Test
    public void two_distinct_event_listeners_should_receive_the_event_after_publication() throws InterruptedException {
        // Given
        final CountDownLatch countDownLatch = new CountDownLatch(2);

        final DomainA.EventA event = new DomainA.EventA();

        final DomainA.EventListenerA eventListenerAOfDomainA = prepareEventListenerAOfDomainA(countDownLatch, event);
        final DomainB.EventListenerA eventListenerAOfDomainB = prepareEventListenerAOfDomainB(countDownLatch, event);

        eventBusRuleA.subscribe(eventListenerAOfDomainA, eventListenerAOfDomainB);

        // When
        eventBusRuleA.publish(event);
        countDownLatch.await(TIMEOUT, TimeUnit.MILLISECONDS);

        // Then
        verify(eventListenerAOfDomainA).handle(any(DomainA.EventA.class));
        verify(eventListenerAOfDomainB).handle(any(DomainA.EventA.class));
    }

    @Test
    public void two_event_listeners_defined_by_the_same_domain_should_receive_the_event_only_one_time_after_publication() throws InterruptedException {
        // Given
        final CountDownLatch countDownLatch = new CountDownLatch(2);

        final EventB event = new EventB();

        final DomainA.EventListenerB eventListenerBOfDomainA = prepareEventListenerBOfDomainA(countDownLatch, event);
        final DomainA.EventListenerB eventListenerBOfDomainAb = prepareEventListenerBOfDomainA(countDownLatch, event);

        eventBusRuleA.subscribe(eventListenerBOfDomainA);
        eventBusRuleB.subscribe(eventListenerBOfDomainAb);

        // When
        eventBusRuleA.publish(event);
        countDownLatch.await(TIMEOUT, TimeUnit.MILLISECONDS);

        // Then

        assertEquals(1, countDownLatch.getCount());
        assertTrue(
                (0 == eventListenerBOfDomainA.expectedEvents.size() && 1 == eventListenerBOfDomainAb.expectedEvents.size()) ||
                        (1 == eventListenerBOfDomainA.expectedEvents.size() && 0 == eventListenerBOfDomainAb.expectedEvents.size())
        );
    }

    private static DomainA.EventListenerB prepareEventListenerBOfDomainA(final CountDownLatch countDownLatch, final EventB... events) {
        final Queue<EventB> expectedEventsB = Queues.newArrayDeque();
        Collections.addAll(expectedEventsB, events);
        return spy(new DomainA.EventListenerB(countDownLatch, expectedEventsB));
    }

    private static DomainA.EventListenerC prepareEventListenerCOfDomainA(final CountDownLatch countDownLatch, final DomainA.EventC... events) {
        final Queue<DomainA.EventC> expectedEventsB = Queues.newArrayDeque();
        Collections.addAll(expectedEventsB, events);
        return spy(new DomainA.EventListenerC(countDownLatch, expectedEventsB));
    }

    private static DomainA.EventListenerA prepareEventListenerAOfDomainA(final CountDownLatch countDownLatch, final DomainA.EventA... events) {
        final Queue<DomainA.EventA> expectedEventsA = Queues.newArrayDeque();
        Collections.addAll(expectedEventsA, events);
        return spy(new DomainA.EventListenerA(countDownLatch, expectedEventsA));
    }

    private static DomainB.EventListenerA prepareEventListenerAOfDomainB(final CountDownLatch countDownLatch, final DomainA.EventA... events) {
        final Queue<DomainA.EventA> expectedEventsA = Queues.newArrayDeque();
        Collections.addAll(expectedEventsA, events);
        return spy(new DomainB.EventListenerA(countDownLatch, expectedEventsA));
    }
}
