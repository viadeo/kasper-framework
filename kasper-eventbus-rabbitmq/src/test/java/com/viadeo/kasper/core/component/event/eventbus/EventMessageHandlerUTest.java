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
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.eventbus;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.rabbitmq.client.Channel;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.api.response.KasperResponse;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import com.viadeo.kasper.core.component.event.listener.EventMessage;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.axonframework.domain.GenericEventMessage;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventMessageHandlerUTest {

    private static class TestEvent implements Event {}
    private static class TestEventListener extends AutowiredEventListener<TestEvent> {

        private EventResponse eventResponse = EventResponse.success();
        private RuntimeException exception = null;

        @Override
        public EventResponse handle(EventMessage message) {
            if (exception != null) {
                throw exception;
            }
            return eventResponse;
        }

        public void setResponse(EventResponse eventResponse) {
            this.eventResponse = eventResponse;
        }

        private void setException(RuntimeException exception) {
            this.exception = exception;
        }
    }

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Mock
    private MessageConverter messageConverter;

    @Mock
    private MessageRecoverer messageRecoverer;

    @Mock
    private MessageProperties messageProperties;

    @Mock
    private Message message;

    @Mock
    private Channel channel;

    private TestEventListener eventListener;

    private EventMessageHandler handler;

    @Before
    public void setUp() throws Exception {
        GenericEventMessage<TestEvent> eventMessage = new GenericEventMessage<>(new TestEvent(), Maps.<String,Object>newHashMap());
        when(messageConverter.fromMessage(any(Message.class))).thenReturn(eventMessage);
        when(message.getMessageProperties()).thenReturn(messageProperties);

        eventListener = new TestEventListener();

        MetricRegistry metricRegistry = new MetricRegistry();
        KasperMetrics.setMetricRegistry(metricRegistry);

        handler = new EventMessageHandler(messageConverter, eventListener, metricRegistry, messageRecoverer, 5, 1);
    }

    @Test
    public void onMessage_withUnexpectedErrorDuringConversion_shouldNackAndRecover() throws Exception {
        // Given
        Long deliveryTag = 1L;
        when(messageProperties.getDeliveryTag()).thenReturn(deliveryTag);
        doThrow(new RuntimeException("Fake exception")).when(messageConverter).fromMessage(message);

        // When
        handler.onMessage(message, channel);

        // Then
        verify(channel).basicNack(deliveryTag, false, false);
        verify(messageRecoverer).recover(eq(message), any(Throwable.class));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void onMessage_withIgnoredResponse_shouldAck() throws Exception {
        // Given
        Long deliveryTag = 2L;
        when(messageProperties.getDeliveryTag()).thenReturn(deliveryTag);
        eventListener.setResponse(EventResponse.ignored());

        // When
        handler.onMessage(message, channel);

        // Then
        verify(channel).basicAck(deliveryTag, false);
        verify(messageRecoverer, never()).recover(eq(message), any(Throwable.class));
    }

    @Test
    public void onMessage_withSuccessResponse_shouldAck() throws Exception {
        // Given
        Long deliveryTag = 3L;
        when(messageProperties.getDeliveryTag()).thenReturn(deliveryTag);

        // When
        handler.onMessage(message, channel);

        // Then
        verify(channel).basicAck(deliveryTag, false);
        verify(messageRecoverer, never()).recover(eq(message), any(Throwable.class));
    }

    @Test
    public void onMessage_withErrorResponse_shouldAck() throws Exception {
        // Given
        Long deliveryTag = 4L;
        when(messageProperties.getDeliveryTag()).thenReturn(deliveryTag);
        eventListener.setResponse(EventResponse.error(new KasperReason(CoreReasonCode.CONFLICT, "fake")));

        // When
        handler.onMessage(message, channel);

        // Then
        verify(channel).basicAck(deliveryTag, false);
        verify(messageRecoverer, never()).recover(eq(message), any(Throwable.class));
    }

    @Test
    public void onMessage_withFailureResponse_throwException() throws Exception {
        // Given
        Long deliveryTag = 5L;
        when(messageProperties.getDeliveryTag()).thenReturn(deliveryTag);
        eventListener.setResponse(EventResponse.failure(new KasperReason(CoreReasonCode.CONFLICT, "fake")));

        // Expect
        thrown.expect(MessageHandlerException.class);
        thrown.expectMessage("fake");

        // When
        handler.onMessage(message, channel);
    }

    @Test
    public void onMessage_withFailureResponse_withAttemptMax_shouldNackAndRecover() throws Exception {
        // Given
        Long deliveryTag = 6L;
        when(messageProperties.getDeliveryTag()).thenReturn(deliveryTag);
        when(messageProperties.getHeaders()).thenReturn(ImmutableMap.<String, Object>builder().put("X-KASPER-NB-ATTEMPT", 10).build());
        eventListener.setResponse(EventResponse.failure(new KasperReason(CoreReasonCode.CONFLICT, "fake")));

        // When
        handler.onMessage(message, channel);

        // Then
        verify(channel).basicNack(deliveryTag, false, false);
        verify(messageRecoverer).recover(eq(message), any(Throwable.class));
    }

    @Test
    public void onMessage_withTemporarilyUnavailableResponse_throwException() throws Exception {
        // Given
        Long deliveryTag = 6L;
        when(messageProperties.getDeliveryTag()).thenReturn(deliveryTag);
        eventListener.setResponse(EventResponse.temporarilyUnavailable(new KasperReason(CoreReasonCode.CONFLICT, "fake")));

        // Expect
        thrown.expect(MessageHandlerException.class);
        thrown.expectMessage("fake");

        // When
        handler.onMessage(message, channel);
    }

    @Test
    public void onMessage_withTemporarilyUnavailableResponse_withAttemptMax_shouldNakWithRequeue() throws Exception {
        // Given
        Long deliveryTag = 6L;
        when(messageProperties.getDeliveryTag()).thenReturn(deliveryTag);
        when(messageProperties.getHeaders()).thenReturn(ImmutableMap.<String, Object>builder().put("X-KASPER-NB-ATTEMPT", 10).build());
        when(messageProperties.getTimestamp()).thenReturn(DateTime.now().plusHours(5).toDate());
        eventListener.setResponse(EventResponse.temporarilyUnavailable(new KasperReason(CoreReasonCode.CONFLICT, "fake")));

        // When
        handler.onMessage(message, channel);

        // Then
        verify(channel).basicNack(deliveryTag, false, true);
        verify(messageRecoverer, never()).recover(eq(message), any(Throwable.class));
    }

    @Test
    public void onMessage_withTemporarilyUnavailableResponse_withExpiredMessage_withAttemptMax_shouldNakAndRecover() throws Exception {
        // Given
        Long deliveryTag = 6L;
        when(messageProperties.getDeliveryTag()).thenReturn(deliveryTag);
        when(messageProperties.getHeaders()).thenReturn(ImmutableMap.<String, Object>builder().put("X-KASPER-NB-ATTEMPT", 10).build());
        eventListener.setResponse(EventResponse.temporarilyUnavailable(new KasperReason(CoreReasonCode.CONFLICT, "fake")));

        // When
        handler.onMessage(message, channel);

        // Then
        verify(channel).basicNack(deliveryTag, false, true);
        verify(messageRecoverer, never()).recover(eq(message), any(Throwable.class));
    }

    @Test
    public void onMessage_withUnknownStatus_shouldNackAndRecover() throws Exception {
        // Given
        eventListener.setResponse(new EventResponse(KasperResponse.Status.ACCEPTED, new KasperReason("", "")));

        Long deliveryTag = 9L;
        when(messageProperties.getDeliveryTag()).thenReturn(deliveryTag);

        // When
        handler.onMessage(message, channel);

        // Then
        verify(channel).basicNack(deliveryTag, false, false);
        verify(messageRecoverer).recover(eq(message), any(Throwable.class));
    }

    @Test
    public void onMessage_withUnexpectedExceptionDuringHandle_shouldNackAndRecover() throws Exception {
        // Given
        eventListener.setException(new RuntimeException("bazinga!"));

        // Then
        thrown.expect(MessageHandlerException.class);
        thrown.expectMessage("bazinga!");

        // When
        handler.onMessage(message, channel);
    }
}
