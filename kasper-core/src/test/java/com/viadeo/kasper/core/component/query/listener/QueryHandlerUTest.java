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
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.query.AutowiredQueryHandler;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class QueryHandlerUTest {

    @Test(expected = NullPointerException.class)
    public void publish_withNullAsEvent_shouldThrowException() {
        // Given
        final AutowiredQueryHandler<Query, QueryResult> handler = new AutowiredQueryHandler<Query, QueryResult>() { };
        final Event event = null;

        // When
        handler.publish(Contexts.empty(), event);

        // Then throws an exception
    }

    @Test
    public void publish_withEvent_shouldPublishAMessageOnTheEventBus() {
        // Given
        final KasperEventBus eventBus = mock(KasperEventBus.class);

        final AutowiredQueryHandler<Query, QueryResult> handler = new AutowiredQueryHandler<Query, QueryResult>() { };
        handler.setEventBus(eventBus);

        final Context context = Contexts.empty();
        final Event event = new Event() { };

        // When
        handler.publish(context, event);

        // Then
        verify(eventBus).publish(context, event);
        verifyNoMoreInteractions(eventBus);
    }

}
