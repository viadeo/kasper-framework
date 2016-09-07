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
package com.viadeo.kasper.test.platform;

import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.event.listener.EventMessage;
import com.viadeo.kasper.test.platform.validator.KasperFixtureEventResultValidator;
import com.viadeo.kasper.test.platform.validator.base.DefaultBaseValidator;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Set;

import static com.viadeo.kasper.test.platform.KasperMatcher.equalTo;
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

            final ArgumentCaptor<EventMessage> captor = ArgumentCaptor.forClass(EventMessage.class);
            verify(eventListener).handle(captor.capture());

            final List<Event> events = Lists.newArrayList();
            for (final EventMessage eventMessage : captor.getAllValues()) {
                events.add(eventMessage.getEvent());
            }

            assertEquals(platform().getRecordedEvents(eventListener.getInputClass()), events);

            remainingListenerClasses.remove(listenerClass);
        }

        for (final Class listenerClass : remainingListenerClasses) {
            verify(platform().listeners.get(listenerClass), never()).handle(any(EventMessage.class));
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public KasperFixtureEventResultValidator expectZeroEventNotification() {
        for (final EventListener eventListener : platform().listeners.values()) {
            verify(eventListener, never()).handle(any(EventMessage.class));
        }
        return this;
    }

    @Override
    public KasperFixtureEventResultValidator expectExactSequenceOfCommands(final Command... commands) {
        final List<Command> actualCommands = platform().recordedCommands;
        assertEquals(commands.length, actualCommands.size());

        for (int i = 0; i < commands.length; i++) {
            assertTrue(equalTo(commands[i]).matches(actualCommands.get(i)));
        }
        return this;
    }

}
