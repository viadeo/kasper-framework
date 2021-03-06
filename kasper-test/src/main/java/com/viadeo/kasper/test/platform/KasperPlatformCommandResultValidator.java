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

import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.test.platform.validator.KasperFixtureCommandResultValidator;
import com.viadeo.kasper.test.platform.validator.KasperFixtureEventResultValidator;
import com.viadeo.kasper.test.platform.validator.base.DefaultBaseValidator;
import org.axonframework.test.AxonAssertionError;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.viadeo.kasper.api.response.KasperResponse.Status.*;
import static com.viadeo.kasper.test.platform.KasperMatcher.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * FIXME: add better debugging information
 */
public class KasperPlatformCommandResultValidator
        extends DefaultBaseValidator
        implements KasperFixtureCommandResultValidator, KasperFixtureEventResultValidator<KasperPlatformCommandResultValidator> {

    private final KasperPlatformListenedEventsValidator eventResultValidator;

    // ------------------------------------------------------------------------

    KasperPlatformCommandResultValidator(
            final KasperPlatformFixture.RecordingPlatform platform,
            final CommandResponse response,
            final Exception exception) {
        super(platform, response, exception);
        this.eventResultValidator = new KasperPlatformListenedEventsValidator(platform, exception);
    }

    // ------------------------------------------------------------------------

    private Iterator<Event> _expectSequenceOfEvents(final Event... expectedEvents) {
        if (expectedEvents.length != platform().recordedEvents.size()) {
            reporter().reportWrongEvent(
                    platform().recordedEvents,
                    Arrays.asList(expectedEvents),
                    exception()
            );
        }

        final Iterator<Event> iterator = platform().recordedEvents.iterator();
        for (final Event expectedEvent : expectedEvents) {
            final Event actualEvent = iterator.next();
            if ( ! equalTo(expectedEvent).matches(actualEvent)) {
                reporter().reportWrongEvent(
                        platform().recordedEvents,
                        Arrays.asList(expectedEvents),
                        exception()
                );
            }
        }

        return iterator;
    }

    @Override
    public KasperPlatformCommandResultValidator expectSequenceOfEvents(final Event... expectedEvents) {
        _expectSequenceOfEvents(expectedEvents);
        return this;
    }

    @Override
    public KasperPlatformCommandResultValidator expectExactSequenceOfEvents(final Event... expectedEvents) {
        final Iterator<Event> iterator = _expectSequenceOfEvents(expectedEvents);
        if (iterator.hasNext()) {
            reporter().reportWrongEvent(
                    platform().recordedEvents,
                    Arrays.asList(expectedEvents),
                    exception()
            );
        }
        return this;
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperPlatformCommandResultValidator expectReturnResponse(final CommandResponse commandResponse) {
        expectReturnValue(equalTo(commandResponse));
        return this;
    }

    @Override
    public KasperPlatformCommandResultValidator expectReturnOK() {
        if (null != exception()) {
            throw new RuntimeException("Unexpected exception", exception());
        }
        if ((null == response()) ||  ! OK.equals(((CommandResponse) response()).getStatus())) {
            throw new AxonAssertionError("Command did not answered OK");
        }
        return this;
    }

    public KasperPlatformCommandResultValidator expectReturnError() {
        if (null != exception()) {
            throw new RuntimeException("Unexpected exception", exception());
        }
        if ((null == response()) ||  ! ERROR.equals(((CommandResponse) response()).getStatus())) {
            throw new AxonAssertionError("Command did not answered an ERROR");
        }
        return this;
    }

    public KasperPlatformCommandResultValidator expectReturnRefused() {
        if (null != exception()) {
            throw new RuntimeException("Unexpected exception", exception());
        }
        if ((null == response()) ||  ! REFUSED.equals(((CommandResponse) response()).getStatus())) {
            throw new AxonAssertionError("Command did not answered an ERROR");
        }
        return this;
    }

    @Override
    public KasperFixtureCommandResultValidator expectReturnError(final KasperReason reason) {
        expectReturnValue(equalTo(CommandResponse.error(reason)));
        return this;
    }

    @Override
    public KasperFixtureCommandResultValidator expectReturnRefused(final KasperReason reason) {
        expectReturnValue(equalTo(CommandResponse.error(reason)));
        return this;
    }

    public KasperFixtureCommandResultValidator expectReturnError(final String code) {
        if (null != exception()) {
            throw new RuntimeException("Unexpected exception", exception());
        }
        if ((null == response())
                || (null == ((CommandResponse) response()).getReason())
                || ! ERROR.equals(((CommandResponse) response()).getStatus())
                || ! ((CommandResponse) response()).getReason().getCode().contentEquals(code)) {
            throw new AxonAssertionError(
                    "Command did not answered the expected error code"
            );
        }
        return this;
    }

    public KasperFixtureCommandResultValidator expectReturnRefused(final String code) {
        if (null != exception()) {
            throw new RuntimeException("Unexpected exception", exception());
        }
        if ((null == response())
                || (null == ((CommandResponse) response()).getReason())
                || ! REFUSED.equals(((CommandResponse) response()).getStatus())
                || ! ((CommandResponse) response()).getReason().getCode().contentEquals(code)) {
            throw new AxonAssertionError(
                    "Command did not answered the expected error code"
            );
        }
        return this;
    }

    public KasperFixtureCommandResultValidator expectReturnError(final CoreReasonCode code) {
        if (null != exception()) {
            throw new RuntimeException("Unexpected exception", exception());
        }
        if ((null == response())
                || (null == ((CommandResponse) response()).getReason())
                || ! ERROR.equals(((CommandResponse) response()).getStatus())
                || ! ((CommandResponse) response()).getReason().getCode().contentEquals(code.name())) {
            throw new AxonAssertionError(
                    "Command did not answered the expected error code"
            );
        }
        return this;
    }

    public KasperFixtureCommandResultValidator expectReturnRefused(final CoreReasonCode code) {
        if (null != exception()) {
            throw new RuntimeException("Unexpected exception", exception());
        }
        if ((null == response())
                || (null == ((CommandResponse) response()).getReason())
                || ! REFUSED.equals(((CommandResponse) response()).getStatus())
                || ! ((CommandResponse) response()).getReason().getCode().contentEquals(code.name())) {
            throw new AxonAssertionError(
                    "Command did not answered the expected error code"
            );
        }
        return this;
    }

    @Override
    public KasperPlatformCommandResultValidator expectExactSequenceOfCommands(final Command... commands) {
        final List<Command> actualCommands = platform().recordedCommands;
        assertEquals(commands.length, actualCommands.size() - 1);

        for (int i = 0; i < commands.length; i++) {
            assertTrue(equalTo(commands[i]).matches(actualCommands.get(i + 1)));
        }

        return this;
    }

    @Override
    public KasperPlatformCommandResultValidator expectEventNotificationOn(final Class... eventListenerClasses) {
        eventResultValidator.expectEventNotificationOn(eventListenerClasses);
        return this;
    }

    @Override
    public KasperPlatformCommandResultValidator expectZeroEventNotification() {
        eventResultValidator.expectZeroEventNotification();
        return this;
    }

}
