package com.viadeo.kasper.event;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Objects;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.axonframework.domain.GenericEventMessage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class EventListenerUTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private EventListener<TestEvent> listener;

    @Before
    public void setUp() throws Exception {
        KasperMetrics.setMetricRegistry(new MetricRegistry());
        listener = spy(new TestEventListener());
    }

    @Test
    public void handle_withSuccessResponse_isOk() {
        // Given
        when(listener.handle(any(Context.class), any(TestEvent.class))).thenReturn(EventResponse.success());
        GenericEventMessage<TestEvent> eventMessage = new GenericEventMessage<>(new TestEvent(), DefaultContextBuilder.get().asMetaDataMap());

        // When
        listener.handle(eventMessage);

        // Then
        verify(listener).handle(any(Context.class), any(TestEvent.class));
    }

    @Test
    public void handle_withIgnoredResponse_isOk() {
        // Given
        when(listener.handle(any(Context.class), any(TestEvent.class))).thenReturn(EventResponse.ignored());
        GenericEventMessage<TestEvent> eventMessage = new GenericEventMessage<>(new TestEvent(), DefaultContextBuilder.get().asMetaDataMap());

        // When
        listener.handle(eventMessage);

        // Then
        verify(listener).handle(any(Context.class), any(TestEvent.class));
    }

    @Test
    public void handle_withUnexpectedException_propagatesException() {
        // Given
        doThrow(new RuntimeException("bazinga!")).when(listener).handle(any(Context.class), any(TestEvent.class));
        GenericEventMessage<TestEvent> eventMessage = new GenericEventMessage<>(new TestEvent(), DefaultContextBuilder.get().asMetaDataMap());

        // Then
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("");

        // When
        listener.handle(eventMessage);
    }

    @Test
    public void handle_withRejectedResponse_throwException() {
        // Given
        when(listener.handle(any(Context.class), any(TestEvent.class))).thenReturn(EventResponse.rejected(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, "bazinga!!")));
        GenericEventMessage<TestEvent> eventMessage = new GenericEventMessage<>(new TestEvent(), DefaultContextBuilder.get().asMetaDataMap());

        // Then
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Rejected event class com.viadeo.kasper.event.EventListenerUTest$TestEvent, <event=TestEvent{}> <messages=bazinga!!>");

        // When
        listener.handle(eventMessage);
    }

    @Test
    public void handle_withRejectedResponse_containingExceptionInReason_wrapException() {
        // Given
        RuntimeException exception = new RuntimeException("Fake exception");
        when(listener.handle(any(Context.class), any(TestEvent.class))).thenReturn(EventResponse.rejected(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, exception)));
        GenericEventMessage<TestEvent> eventMessage = new GenericEventMessage<>(new TestEvent(), DefaultContextBuilder.get().asMetaDataMap());

        // Then
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Rejected event class com.viadeo.kasper.event.EventListenerUTest$TestEvent, <event=TestEvent{}>");

        // When
        listener.handle(eventMessage);
    }

    private static class TestEventListener extends EventListener<TestEvent> { }

    private static class TestEvent implements Event {
        @Override
        public String toString() {
            return Objects.toStringHelper(this).toString();
        }
    }
}
