// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
package com.viadeo.kasper.core.component.event.eventbus;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
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
import static org.mockito.ArgumentMatchers.any;
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
