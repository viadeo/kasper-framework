package com.viadeo.kasper.core.component.event.eventbus;

import com.codahale.metrics.MetricRegistry;
import com.typesafe.config.Config;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.eventbus.AMQPCluster;
import com.viadeo.kasper.core.component.event.eventbus.AMQPComponentNameFormatter;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.event.eventbus.spring.EventBusConfiguration;
import com.viadeo.kasper.core.component.event.eventbus.spring.RabbitMQConfiguration;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.axonframework.domain.GenericEventMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static com.viadeo.kasper.core.component.event.eventbus.AMQPTopologyITest.TestEvent;
import static com.viadeo.kasper.core.component.event.eventbus.AMQPTopologyITest.TestEventListener;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        AMQPClusterITest.TestConfiguration.class,
        RabbitMQConfiguration.class,
        EventBusConfiguration.AmqpClusterConfiguration.class
})
@ActiveProfiles(profiles = "rabbitmq")
public class MessageHandlerITest {

    public static final int TIMEOUT = 500;

    private static TestEventListener eventListenerWrapper;
    private static final EventListener<TestEvent> mockedEventListener = mock(EventListener.class);

    @Inject
    private AMQPCluster cluster;

    @Inject
    private MessageConverter messageConverter;

    @Inject
    private RabbitTemplate rabbitTemplate;

    @Inject
    private AMQPComponentNameFormatter nameFormatter;

    @Inject
    private Config config;

    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void init() throws Exception {
        KasperMetrics.setMetricRegistry(new MetricRegistry());
        eventListenerWrapper = new TestEventListener(mockedEventListener);
    }

    @Before
    public void setUp() throws Exception {
        reset((EventListener)mockedEventListener);
        cluster.subscribe(eventListenerWrapper);
        cluster.start();
    }

    @After
    public void tearDown() throws Exception {
        cluster.unsubscribe(eventListenerWrapper);
    }

    @Test
    public void handle_withoutTrouble_isOk() throws Exception {
        // Given
        when(mockedEventListener.handle(any(Context.class), any(TestEvent.class))).thenReturn(EventResponse.success());
        ArgumentCaptor<TestEvent> eventCaptor = ArgumentCaptor.forClass(TestEvent.class);
        TestEvent testEvent = new TestEvent();

        // When
        cluster.publish(new GenericEventMessage<>(testEvent));

        synchronized (mockedEventListener) {
            mockedEventListener.wait(TIMEOUT);
        }

        // Then
        verify(mockedEventListener).handle(any(Context.class), eventCaptor.capture());
        assertEquals(1, eventCaptor.getAllValues().size());
        assertEquals(testEvent, eventCaptor.getValue());
    }

    @Test
    public void handle_withUnexpectedException_shouldRetry5TimesBeforeRequeueInDeadLetter() throws Exception {
        // Given
        doThrow(new RuntimeException("Bazinga!!")).when(mockedEventListener).handle(any(Context.class), any(TestEvent.class));

        ArgumentCaptor<TestEvent> eventCaptor = ArgumentCaptor.forClass(TestEvent.class);

        TestEvent testEvent = new TestEvent();

        // When
        cluster.doPublish(new GenericEventMessage<>(testEvent));

        synchronized (mockedEventListener) {
            mockedEventListener.wait(TIMEOUT);
        }

        // Then
        verify(mockedEventListener, times(5)).handle(any(Context.class), eventCaptor.capture());
        assertEquals(5, eventCaptor.getAllValues().size());
        for (TestEvent event : eventCaptor.getAllValues()) {
            assertEquals(testEvent, event);
        }

        String clusterName = config.getString("runtime.eventbus.amqp.clusterName");
        String exchangeName = nameFormatter.getFullExchangeName(cluster.getExchangeDescriptor().name, cluster.getExchangeDescriptor().version);
        Message message = rabbitTemplate.receive(nameFormatter.getDeadLetterQueueName(exchangeName, clusterName, eventListenerWrapper));

        assertNotNull(message);
        assertEquals(testEvent, ((GenericEventMessage) messageConverter.fromMessage(message)).getPayload());
    }

}
