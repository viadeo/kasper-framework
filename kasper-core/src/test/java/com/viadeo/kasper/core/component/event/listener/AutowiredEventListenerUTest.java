// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.listener;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.id.KasperID;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class AutowiredEventListenerUTest {

    @Test
    public void handle_an_event_message_should_set_the_current_context() {
        EventListener<Event> eventListener = new AutowiredEventListener<Event>() {
            @Override
            public EventResponse handle(Context context, Event event) {
                this.getContext();
                return EventResponse.success();
            }
        };

        EventResponse response = eventListener.handle(new EventMessage<>(
                Optional.<KasperID>absent(),
                DateTime.now(),
                Contexts.builder(UUID.randomUUID()).build(),
                mock(Event.class)
        ));

        assertTrue(response.isOK());
    }
}
