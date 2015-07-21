package com.viadeo.kasper.core.component.eventbus;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.EventListener;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.StandardEnvironment;
import rabbitmq.mgmt.BindingOperations;
import rabbitmq.mgmt.QueueOperations;
import rabbitmq.mgmt.RabbitMgmtService;
import rabbitmq.mgmt.model.Binding;
import rabbitmq.mgmt.model.Queue;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.amqp.core.Binding.DestinationType;

public class QueueFinderUTest {

    private StandardEnvironment environment;

    public static class Fake_EventListener extends EventListener<Event> {
        @Override
        public EventResponse handle(Context context, Event event) {
            return EventResponse.success();
        }
    }

    public static class FakeEventListener extends EventListener<Event> {
        @Override
        public EventResponse handle(Context context, Event event) {
            return EventResponse.success();
        }
    }

    @Profile("!test")
    public static class AnnotatedFakeEventListener extends EventListener<Event> {
        @Override
        public EventResponse handle(Context context, Event event) {
            return EventResponse.success();
        }
    }

    private String vhost;
    private String fullExchangeName;
    private QueueFinder queueFinder;
    private List<Queue> queues;
    private List<Binding> bindings;
    private AMQPComponentNameFormatter amqpComponentNameFormatter;

    @Before
    public void setUp() throws Exception {
        amqpComponentNameFormatter = new AMQPComponentNameFormatter();
        vhost = "/";
        fullExchangeName = amqpComponentNameFormatter.getFullExchangeName("exchangeName", "1");
        queues = Lists.newArrayList();
        bindings = Lists.newArrayList();

        QueueOperations queueOperations = mock(QueueOperations.class);
        when(queueOperations.allOnVHost(vhost)).thenReturn(Optional.<Collection<Queue>>of(queues));

        BindingOperations bindingOperations = mock(BindingOperations.class);
        when(bindingOperations.get(eq(vhost), anyString(), anyString(), eq(false))).thenReturn(Optional.<Collection<Binding>>of(bindings));

        RabbitMgmtService rabbitMgmtService = mock(RabbitMgmtService.class);
        when(rabbitMgmtService.queues()).thenReturn(queueOperations);
        when(rabbitMgmtService.bindings()).thenReturn(bindingOperations);

        environment = new StandardEnvironment();

        queueFinder = new QueueFinder(
                amqpComponentNameFormatter,
                rabbitMgmtService,
                vhost,
                environment,
                fullExchangeName,
                fullExchangeName + "_default_dead-letter"
        );
    }

    @Test
    public void getObsoleteQueueNames_withNoQueue_returnNothing() throws Exception {
        // Given no queue

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(0, obsoleteQueueNames.size());
    }

    @Test
    public void getObsoleteQueueNames_withOneQueue_returnNothing() throws Exception {
        // Given
        queues.add(new Queue(amqpComponentNameFormatter.getQueueName(fullExchangeName, "clusterName", new FakeEventListener())));

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(0, obsoleteQueueNames.size());
    }

    @Test
    public void getObsoleteQueueNames_withOneDeadLetterQueue_returnNothing() throws Exception {
        // Given
        queues.add(new Queue(amqpComponentNameFormatter.getQueueName(fullExchangeName, "clusterName", FakeEventListener.class.getName() + "_dead-letter")));

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(0, obsoleteQueueNames.size());
    }

    @Test
    public void getObsoleteQueueNames_withOneQueue_notMatchingQueueName_returnNothing() throws Exception {
        // Given
        queues.add(new Queue("miaou"));

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(0, obsoleteQueueNames.size());
    }

    @Test
    public void getObsoleteQueueNames_withOneQueue_notRespectNamingConvention_returnNothing() throws Exception {
        // Given
        queues.add(new Queue(fullExchangeName + "_clusterName_" + Fake_EventListener.class.getName()));

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(0, obsoleteQueueNames.size());
    }

    @Test
    public void getObsoleteQueueNames_withOneObsoleteQueue_returnTheQueueName() throws Exception {
        // Given
        String queueName = amqpComponentNameFormatter.getQueueName(fullExchangeName, "clusterName", "miaou");
        queues.add(new Queue(queueName));

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(1, obsoleteQueueNames.size());

        QueueInfo queueInfo = obsoleteQueueNames.iterator().next();
        assertEquals(queueName, queueInfo.getQueueName());
        assertEquals(fullExchangeName, queueInfo.getExchangeName());
        assertEquals("miaou", queueInfo.getEventListenerClassName());
        assertFalse(queueInfo.isDeadLetter());
    }

    @Test
    public void getObsoleteQueueNames_withOneObsoleteDeadLetterQueue_returnTheQueueName() throws Exception {
        // Given
        String queueName = amqpComponentNameFormatter.getQueueName(fullExchangeName, "clusterName", "miaou_dead-letter");
        queues.add(new Queue(queueName));

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(1, obsoleteQueueNames.size());

        QueueInfo queueInfo = obsoleteQueueNames.iterator().next();
        assertEquals(queueName, queueInfo.getQueueName());
        assertEquals(fullExchangeName, queueInfo.getExchangeName());
        assertEquals("miaou", queueInfo.getEventListenerClassName());
        assertTrue(queueInfo.isDeadLetter());
    }

    @Test
    public void getObsoleteQueueNames_withTestAsActiveProfile_withOneQueue_returnNothing() throws Exception {
        // Given
        queues.add(new Queue(amqpComponentNameFormatter.getQueueName(fullExchangeName, "clusterName", new FakeEventListener())));
        environment.setActiveProfiles("test");

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(0, obsoleteQueueNames.size());
    }

    @Test
    public void getObsoleteQueueNames_withTestAsActiveProfile_withOneAnnotatedQueue_withTestAsInactiveProfile_returnTheQueueName() throws Exception {
        // Given
        String queueName = amqpComponentNameFormatter.getQueueName(fullExchangeName, "clusterName", new AnnotatedFakeEventListener());
        queues.add(new Queue(queueName));
        environment.setActiveProfiles("test");

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(1, obsoleteQueueNames.size());

        QueueInfo queueInfo = obsoleteQueueNames.iterator().next();
        assertEquals(queueName, queueInfo.getQueueName());
        assertEquals(fullExchangeName, queueInfo.getExchangeName());
        assertEquals(AnnotatedFakeEventListener.class.getName(), queueInfo.getEventListenerClassName());
        assertFalse(queueInfo.isDeadLetter());
    }

    @Test
    public void getObsoleteQueueNames_withQueueOfOutdatedTopology_returnNothing() throws Exception {
        // Given
        String queueName = amqpComponentNameFormatter.getQueueName("outdatedPlatform", "clusterName", new AnnotatedFakeEventListener());
        queues.add(new Queue(queueName));

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(1, obsoleteQueueNames.size());

        QueueInfo queueInfo = obsoleteQueueNames.iterator().next();
        assertEquals(queueName, queueInfo.getQueueName());
        assertEquals("outdatedPlatform", queueInfo.getExchangeName());
        assertEquals(AnnotatedFakeEventListener.class.getName(), queueInfo.getEventListenerClassName());
        assertFalse(queueInfo.isDeadLetter());
    }

    @Test
    public void getObsoleteQueueNames_withFallbackDeadLetterQueue_returnNothing() throws Exception {
        // Given
        queues.add(new Queue(fullExchangeName + "_default_dead-letter"));

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(0, obsoleteQueueNames.size());
    }

    @Test
    public void getQueueBindings_withNoBinding_returnNothing() {
        // Given

        // When
        Collection<org.springframework.amqp.core.Binding> queueBindings = queueFinder.getQueueBindings(fullExchangeName + "_clusterName_queueName");

        // Then
        assertNotNull(queueBindings);
        assertTrue(queueBindings.isEmpty());
    }

    @Test
    public void getQueueBindings_withBinding_returnTheBinding() {
        // Given
        HashMap<String,Object> arguments = Maps.newHashMap();
        String queueName = "queueName";
        bindings.add(new Binding(fullExchangeName, vhost, queueName, "queue", "routing_key", "properties_key", arguments));

        // When
        Collection<org.springframework.amqp.core.Binding> queueBindings = queueFinder.getQueueBindings(
                new QueueInfo(
                        fullExchangeName + "_clusterName_" + queueName,
                        fullExchangeName,
                        queueName,
                        false
                )
        );

        // Then
        assertNotNull(queueBindings);
        assertEquals(1, queueBindings.size());

        org.springframework.amqp.core.Binding binding = queueBindings.iterator().next();
        assertEquals(fullExchangeName, binding.getExchange());
        assertEquals(queueName, binding.getDestination());
        assertEquals(DestinationType.QUEUE, binding.getDestinationType());
        assertEquals("routing_key", binding.getRoutingKey());
        assertEquals(arguments, binding.getArguments());
    }

    @Test
    public void getQueueBindings_withBindingRelatedToADeadLetter_returnTheBinding() {
        // Given
        HashMap<String,Object> arguments = Maps.newHashMap();
        String queueName = "queueName";
        bindings.add(new Binding(fullExchangeName + "_dead-letter", vhost, queueName, "queue", "routing_key", "properties_key", arguments));

        // When
        Collection<org.springframework.amqp.core.Binding> queueBindings = queueFinder.getQueueBindings(
                new QueueInfo(
                        fullExchangeName + "_clusterName_" + queueName + "_dead-letter",
                        fullExchangeName + "_dead-letter",
                        queueName,
                        true
                )
        );

        // Then
        assertNotNull(queueBindings);
        assertEquals(1, queueBindings.size());

        org.springframework.amqp.core.Binding binding = queueBindings.iterator().next();
        assertEquals(fullExchangeName + "_dead-letter", binding.getExchange());
        assertEquals(queueName, binding.getDestination());
        assertEquals(DestinationType.QUEUE, binding.getDestinationType());
        assertEquals("routing_key", binding.getRoutingKey());
        assertEquals(arguments, binding.getArguments());
    }
}
