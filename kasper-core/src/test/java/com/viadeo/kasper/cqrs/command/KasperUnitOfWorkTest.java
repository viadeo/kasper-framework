// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.impl.UnitOfWorkEvent;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.MetaData;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.unitofwork.CurrentUnitOfWork;
import org.hamcrest.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.internal.matchers.VarargMatcher;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

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

    private KasperUnitOfWork uow;

    @Before
    public void setUp(){
        this.uow = KasperUnitOfWork.startAndGet();
    }

    @After
    public void cleanUp(){
        CurrentUnitOfWork.clear(uow);
    }

    @Test
    public void testMacroEvent_OneEventNormalCase() {
        // Given
        final Event event = mock(Event.class);
        final EventMessage message = mock(EventMessage.class);
        final EventBus eventBus = mock(EventBus.class);
        final String eventId = UUID.randomUUID().toString();
        
        final MetaData metadata = MetaData.from(new HashMap<String, Object>() {{
            this.put(Context.METANAME, mock(Context.class));
        }});

        doReturn(eventId).when(message).getIdentifier();
        doReturn(event).when(message).getPayload();
        doReturn(event.getClass()).when(message).getPayloadType();
        doReturn(metadata).when(message).getMetaData();

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
        final KasperUnitOfWork uow = spy(this.uow);

        final Event event1 = mock(Event.class);
        final EventMessage message1 = mock(EventMessage.class);

        final Event event2 = mock(Event.class);
        final EventMessage message2 = mock(EventMessage.class);

        final EventBus eventBus = mock(EventBus.class);

        final String eventId1 = UUID.randomUUID().toString();
        final String eventId2 = UUID.randomUUID().toString();

        final MetaData metadata = MetaData.from(new HashMap<String, Object>() {{
            this.put(Context.METANAME, mock(Context.class));
        }});

        doReturn(eventId1).when(message1).getIdentifier();
        doReturn(event1).when(message1).getPayload();
        doReturn(event1.getClass()).when(message1).getPayloadType();
        doReturn(metadata).when(message1).getMetaData();
        doReturn("eventmessage["+event1.toString()+"]").when(message1).toString();

        doReturn(eventId2).when(message2).getIdentifier();
        doReturn(event2).when(message2).getPayload();
        doReturn(event2.getClass()).when(message2).getPayloadType();
        doReturn(metadata).when(message2).getMetaData();
        doReturn("eventmessage["+event2.toString()+"]").when(message2).toString();

        // doReturn(context).when(context).child();

        // When
        uow.registerForPublication(message1, eventBus);
        uow.registerForPublication(message2, eventBus);
        uow.publishEvents();

        // Then UOW event is set to previous events
        verify(event1).setUOWEventId(anyString());
        verify(event2).setUOWEventId(anyString());

        // Then UOW event is registered to publication
        final ArgumentCaptor<EventMessage> argMessage = ArgumentCaptor.forClass(EventMessage.class);
        verify(uow, times(3)).registerForPublication(argMessage.capture(), eq(eventBus));

        // Then UOW event contains all previously sent events
        final EventMessage uowEventMessage = argMessage.getAllValues().get(2);
        assertEquals(UnitOfWorkEvent.class, uowEventMessage.getPayloadType());
        final UnitOfWorkEvent uowEvent = (UnitOfWorkEvent) uowEventMessage.getPayload();
        final List<String> eventIds = uowEvent.getEventIds();
        assertEquals(2, eventIds.size());
        assertEquals(message1.getIdentifier(), eventIds.get(0));
        assertEquals(message2.getIdentifier(), eventIds.get(1));

        // Then all events are published to the bus
        verify(eventBus).publish(argThat(new ArrayOfMessagesMatcher(
                new ArgumentMatcher[]{
                        new ContainsEventMatcher(event1),
                        new ContainsEventMatcher(event2),
                        new ContainsEventTypeMatcher(UnitOfWorkEvent.class)
                }
        )));
    }

}
