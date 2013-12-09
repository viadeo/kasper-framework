package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.event.Event;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.EventBus;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class QueryHandlerUTest {

    @Test(expected = NullPointerException.class)
    public void publish_withNullAsEvent_shouldThrowException() {
        // Given
        QueryHandler<Query, QueryResult> handler = new QueryHandler<Query, QueryResult>() { };
        Event event = null;

        // When
        handler.publish(event);

        // Then throws an exception
    }

    @Test
    public void publish_withEvent_shouldPublishAMessageOnTheEventBus() {
        // Given
        EventBus eventBus = mock(EventBus.class);

        QueryHandler<Query, QueryResult> handler = new QueryHandler<Query, QueryResult>() { };
        handler.setEventBus(eventBus);

        Event event = new Event() { };

        // When
        handler.publish(event);

        // Then
        verify(eventBus).publish(any(GenericEventMessage.class));
        verifyNoMoreInteractions(eventBus);
    }
}
