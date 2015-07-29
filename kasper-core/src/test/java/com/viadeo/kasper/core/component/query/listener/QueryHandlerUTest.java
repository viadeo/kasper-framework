// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query.listener;

import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.core.component.query.AutowiredQueryHandler;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.EventBus;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class QueryHandlerUTest {

    @Test(expected = NullPointerException.class)
    public void publish_withNullAsEvent_shouldThrowException() {
        // Given
        final AutowiredQueryHandler<Query, QueryResult> handler = new AutowiredQueryHandler<Query, QueryResult>() { };
        final Event event = null;

        // When
        handler.publish(event);

        // Then throws an exception
    }

    @Test
    public void publish_withEvent_shouldPublishAMessageOnTheEventBus() {
        // Given
        final EventBus eventBus = mock(EventBus.class);

        final AutowiredQueryHandler<Query, QueryResult> handler = new AutowiredQueryHandler<Query, QueryResult>() { };
        handler.setEventBus(eventBus);

        final Event event = new Event() { };

        // When
        handler.publish(event);

        // Then
        verify(eventBus).publish(any(GenericEventMessage.class));
        verifyNoMoreInteractions(eventBus);
    }

}
