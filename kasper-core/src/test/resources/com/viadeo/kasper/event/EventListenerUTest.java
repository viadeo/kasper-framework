package com.viadeo.kasper.event;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Objects;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.Contexts;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.axonframework.domain.GenericEventMessage;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
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
    public void handle_withSuccessAsResponse_isOk() {
        // Given
        when(listener.handle(any(Context.class), any(TestEvent.class))).thenReturn(EventResponse.success());
        GenericEventMessage<TestEvent> eventMessage = new GenericEventMessage<>(new TestEvent(), Contexts.empty().asMetaDataMap());

        // When
        listener.handle(eventMessage);

        // Then
        verify(listener).handle(any(Context.class), any(TestEvent.class));
    }

    @Test
    public void handle_withIncompatibleEvent_isOk() {
        // Given
        GenericEventMessage<TestEvent2> eventMessage = new GenericEventMessage<>(new TestEvent2(), Contexts.empty().asMetaDataMap());

        // When
        listener.handle(eventMessage);

        // Then
        verify(listener, never()).handle(any(Context.class), any(TestEvent.class));
    }

    @Test
    public void handle_withErrorAsResponse_throwException() {
        // Given
        when(listener.handle(any(Context.class), any(TestEvent.class))).thenReturn(EventResponse.error(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, "bazinga!!")));
        GenericEventMessage<TestEvent> eventMessage = new GenericEventMessage<>(new TestEvent(), Contexts.empty().asMetaDataMap());

        // Then
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(equalsTo("Failed to handle event class com.viadeo.kasper.event.EventListenerUTest$TestEvent, <event=TestEvent{}> <response=EventResponse{ERROR, KasperReason{_UUID_, INTERNAL_COMPONENT_ERROR, [bazinga!!]}}>"));

        // When
        listener.handle(eventMessage);
    }

    @Test
    public void handle_withFailureAsResponse_throwException() {
        // Given
        when(listener.handle(any(Context.class), any(TestEvent.class))).thenReturn(EventResponse.failure(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, "bazinga!!")));
        GenericEventMessage<TestEvent> eventMessage = new GenericEventMessage<>(new TestEvent(), Contexts.empty().asMetaDataMap());

        // Then
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(equalsTo("Failed to handle event class com.viadeo.kasper.event.EventListenerUTest$TestEvent, <event=TestEvent{}> <response=EventResponse{FAILURE, KasperReason{_UUID_, INTERNAL_COMPONENT_ERROR, [bazinga!!]}}>"));

        // When
        listener.handle(eventMessage);
    }

    @Test
    public void handle_withFailureAsResponse_containingExceptionInReason_wrapException() {
        // Given
        RuntimeException exception = new RuntimeException("Fake exception");
        when(listener.handle(any(Context.class), any(TestEvent.class))).thenReturn(EventResponse.failure(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, exception)));
        GenericEventMessage<TestEvent> eventMessage = new GenericEventMessage<>(new TestEvent(), Contexts.empty().asMetaDataMap());

        // Then
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(equalsTo("Failed to handle event class com.viadeo.kasper.event.EventListenerUTest$TestEvent, <event=TestEvent{}> <response=EventResponse{FAILURE, KasperReason{_UUID_, INTERNAL_COMPONENT_ERROR, [Fake exception]}}>"));

        // When
        listener.handle(eventMessage);
    }

    @Test
    public void handle_withUnexpectedException_propagatesException() {
        // Given
        doThrow(new RuntimeException("bazinga!")).when(listener).handle(any(Context.class), any(TestEvent.class));
        GenericEventMessage<TestEvent> eventMessage = new GenericEventMessage<>(new TestEvent(), Contexts.empty().asMetaDataMap());

        // Then
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(equalsTo("Failed to handle event class com.viadeo.kasper.event.EventListenerUTest$TestEvent, <event=TestEvent{}> <response=EventResponse{FAILURE, KasperReason{_UUID_, INTERNAL_COMPONENT_ERROR, [bazinga!]}}>"));

        // When
        listener.handle(eventMessage);
    }

    private static Matcher<String> equalsTo(final String expected) {
        return new BaseMatcher<String>() {
            @Override
            public boolean matches(Object o) {
                return o.toString().replaceAll("\\w{8}(-\\w{4}){3}-\\w{12}?", "_UUID_").equals(expected);
            }

            @Override
            public void describeTo(Description description) { }
        };
    }

    private static class TestEventListener extends EventListener<TestEvent> { }

    private static class TestEvent implements Event {
        @Override
        public String toString() {
            return Objects.toStringHelper(this).toString();
        }
    }

    private static class TestEvent2 implements Event {
        @Override
        public String toString() {
            return Objects.toStringHelper(this).toString();
        }
    }
}
