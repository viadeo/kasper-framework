// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.test.platform.validator.KasperFixtureEventResultValidator;
import com.viadeo.kasper.test.platform.validator.base.DefaultBaseValidator;
import com.viadeo.kasper.tools.KasperMatcher;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class KasperPlatformListenedEventsValidator
        extends DefaultBaseValidator
        implements KasperFixtureEventResultValidator {

    KasperPlatformListenedEventsValidator(
            final KasperPlatformFixture.RecordingPlatform platform,
            final Exception exception) {
        super(platform, null, exception);
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Override
    public KasperFixtureEventResultValidator expectEventNotificationOn(final Class... listenerClasses) {
        final Set<Class<? extends EventListener>> remainingListenerClasses = platform().listeners.keySet();

        for (final Class listenerClass : listenerClasses) {
            final EventListener eventListener = platform().listeners.get(listenerClass);
            assertNotNull("Unknown event listener : " + listenerClass.getName(), eventListener);

            final ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
            verify(eventListener).handle(any(Context.class), captor.capture());
            assertEquals(platform().getRecordedEvents(eventListener.getEventClass()), captor.getAllValues());

            remainingListenerClasses.remove(listenerClass);
        }

        for (final Class listenerClass : remainingListenerClasses) {
            verify(platform().listeners.get(listenerClass), never()).handle(any(Context.class), any(Event.class));
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public KasperFixtureEventResultValidator expectZeroEventNotification() {
        for (final EventListener eventListener : platform().listeners.values()) {
            verify(eventListener, never()).handle(any(Context.class), any(Event.class));
        }
        return this;
    }

    @Override
    public KasperFixtureEventResultValidator expectExactSequenceOfCommands(final Command... commands) {
        final List<Command> actualCommands = platform().recordedCommands;
        assertEquals(commands.length, actualCommands.size());

        for (int i = 0; i < commands.length; i++) {
            assertTrue(KasperMatcher.equalTo(commands[i]).matches(actualCommands.get(i)));
        }
        return this;
    }

}
