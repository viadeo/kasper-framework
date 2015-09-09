package com.viadeo.kasper.core.component.event.eventbus;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.viadeo.kasper.core.component.event.eventbus.MessageHandler;
import com.viadeo.kasper.core.component.event.eventbus.MessageHandlerException;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.EventListener;
import org.jboss.logging.MDC;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class MessageHandlerUTest {

    private EventListener eventListener;
    private MetricRegistry metricRegistry;

    @Before
    public void setUp() throws Exception {
        eventListener = mock(EventListener.class);
        metricRegistry = new MetricRegistry();
    }

    public static EventMessage createEventMesage() {
        return createEventMesage(new Object(), Maps.<String, Object>newHashMap());
    }

    public static EventMessage createEventMesage(final Object payload, final Map<String,Object> metadata) {
        return new GenericEventMessage<>(UUID.randomUUID().toString(), DateTime.now(), payload, metadata);
    }

    @Test
    public void handleMessage_withEnabledMessageHandling_doHandle() {
        // Given
        boolean enabledMessageHandling = true;
        MessageHandler messageHandler = new MessageHandler(eventListener, metricRegistry, enabledMessageHandling);
        EventMessage message = createEventMesage();

        // When
        messageHandler.handleMessage(message);

        // Then
        verify(eventListener).handle(message);

    }

    @Test
    public void handleMessage_withDisabledMessageHandling_doNotHandle() {
        // Given
        boolean enabledMessageHandling = false;
        MessageHandler messageHandler = new MessageHandler(eventListener, metricRegistry, enabledMessageHandling);
        EventMessage message = createEventMesage();

        // When
        messageHandler.handleMessage(message);

        // Then
        verify(eventListener, never()).handle(message);
    }

    @Test
    public void handleMessage_shouldFillMDC() {
        // Given
        Map<String, Object> metadata = ImmutableMap.<String, Object>builder()
                .put("firstname", "chuck")
                .put("lastname", "norris")
                .build();

        MessageHandler messageHandler = new MessageHandler(eventListener, metricRegistry, true);
        EventMessage message = createEventMesage(new Object(), metadata);

        // When
        messageHandler.handleMessage(message);

        // Then
        assertEquals(metadata, MDC.getMap());
    }

    @Test
    public void handleMessage_withUnexpectedException_throwException() {
        // Given
        doThrow(new RuntimeException("bazinga!!!")).when(eventListener).handle(any(EventMessage.class));
        MessageHandler messageHandler = new MessageHandler(eventListener, metricRegistry, true);
        EventMessage message = createEventMesage();
        Throwable throwable = null;

        try {
            // When
            messageHandler.handleMessage(message);
        } catch (Throwable t) {
            throwable = t;
        }

        // Then
        assertNotNull(throwable);
        assertTrue(throwable instanceof MessageHandlerException);

        MessageHandlerException messageHandlerException = (MessageHandlerException) throwable;
        assertEquals(eventListener.getClass(), messageHandlerException.getSource().get());
    }
}
