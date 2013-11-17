// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.impl.UnitOfWorkEvent;
import com.viadeo.kasper.impl.DefaultKasperId;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.EventBus;
import org.hamcrest.Description;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.internal.matchers.VarargMatcher;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class KasperUnitOfWorkTest {

    private static class ContainsEventTypeMatcher extends ArgumentMatcher<EventMessage> {
        private final Class<? extends Event> eventType;

        public ContainsEventTypeMatcher(final Class<? extends Event> eventType) {
            this.eventType = eventType;
        }

        @Override
        public boolean matches(final Object argument) {
            final Class eventType = ((EventMessage) argument).getPayloadType();
            return this.eventType.isAssignableFrom(eventType);
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("EventMessage with event type <" + eventType.toString() + ">");
        }
    }

    // -----

    private static class ContainsEventMatcher extends ArgumentMatcher<EventMessage> {
        private final Event event;

        public ContainsEventMatcher(final Event event) {
            this.event = event;
        }

        @Override
        public boolean matches(final Object argument) {
            final Event event = (Event) ((EventMessage) argument).getPayload();
            return event.equals(this.event);
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("EventMessage with event <" + event.toString() + ">");
        }
    }

    // -----

    private static class ArrayOfMessagesMatcher
            extends ArgumentMatcher<EventMessage[]>
            implements VarargMatcher {

        private final ArgumentMatcher[] messageMatchers;

        public ArrayOfMessagesMatcher(final ArgumentMatcher[] messageMatchers) {
            this.messageMatchers = messageMatchers;
        }

        @Override
        public boolean matches(final Object argument) {
            final EventMessage[] messages = (EventMessage[]) argument;

            if (messages.length != messageMatchers.length) {
                return false;
            }

            for (int i = 0 ; i < messages.length ; i++) {
                if ( ! messageMatchers[i].matches(messages[i])) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public void describeTo(final Description description) {
            boolean first = true;
            for (final ArgumentMatcher messageMatcher : messageMatchers) {
                if ( ! first) {
                    description.appendText("    ");
                }
                first = false;
                messageMatcher.describeTo(description);
                description.appendText("\n");
            }
        }
    }

    // ------------------------------------------------------------------------

    @Test
    public void testMacroEvent_OneEventNormalCase() {
        // Given
        final KasperUnitOfWork uow = KasperUnitOfWork.startAndGet();
        final Event event = mock(Event.class);
        final EventMessage message = mock(EventMessage.class);
        final EventBus eventBus = mock(EventBus.class);
        final Context context = mock(Context.class);
        final KasperID eventId = DefaultKasperId.random();

        doReturn(eventId).when(event).getId();
        doReturn(event).when(message).getPayload();
        doReturn(event.getClass()).when(message).getPayloadType();
        doReturn(Optional.of(context)).when(event).getContext();

        // When
        uow.registerForPublication(message, eventBus);
        uow.publishEvents();

        // Then
        verify(event).setUOWEventId(eq(eventId));
        verify(eventBus).publish(message);
    }

    @Test
    public void testMacroEvent_MultipleEventsNormalCase() {
        // Given
        final KasperUnitOfWork uow = KasperUnitOfWork.startAndGet();

        final Event event1 = mock(Event.class);
        final EventMessage message1 = mock(EventMessage.class);
        final Event event2 = mock(Event.class);
        final EventMessage message2 = mock(EventMessage.class);

        final EventBus eventBus = mock(EventBus.class);
        final Context context = mock(Context.class);

        final KasperID eventId1 = DefaultKasperId.random();
        final KasperID eventId2 = DefaultKasperId.random();

        doReturn(eventId1).when(event1).getId();
        doReturn(event1).when(message1).getPayload();
        doReturn(event1.getClass()).when(message1).getPayloadType();
        doReturn(Optional.of(context)).when(event1).getContext();
        doReturn("eventmessage["+event1.toString()+"]").when(message1).toString();

        doReturn(eventId2).when(event2).getId();
        doReturn(event2).when(message2).getPayload();
        doReturn(event2.getClass()).when(message2).getPayloadType();
        doReturn(Optional.of(context)).when(event2).getContext();
        doReturn("eventmessage["+event2.toString()+"]").when(message2).toString();

        doReturn(context).when(context).child();

        // When
        uow.registerForPublication(message1, eventBus);
        uow.registerForPublication(message2, eventBus);
        uow.publishEvents();

        // Then
        verify(event1).setUOWEventId(any(KasperID.class));
        verify(event2).setUOWEventId(any(KasperID.class));
        verify(eventBus).publish(argThat(new ArrayOfMessagesMatcher(
            new ArgumentMatcher[] {
                    new ContainsEventMatcher(event1),
                    new ContainsEventMatcher(event2),
                    new ContainsEventTypeMatcher(UnitOfWorkEvent.class)
            }
        )));
    }

}
