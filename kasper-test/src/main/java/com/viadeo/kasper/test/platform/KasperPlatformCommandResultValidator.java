// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.event.IEvent;
import org.axonframework.test.AxonAssertionError;

import java.util.Arrays;
import java.util.Iterator;

import static com.viadeo.kasper.cqrs.command.CommandResponse.Status.*;
import static com.viadeo.kasper.test.matchers.KasperMatcher.equalTo;

/**
 * FIXME: add better debugging information
 */
public class KasperPlatformCommandResultValidator
        extends KasperPlatformResultValidator
        implements KasperFixtureCommandResultValidator {

    // ------------------------------------------------------------------------

    KasperPlatformCommandResultValidator(
            final KasperPlatformFixture.RecordingPlatform platform,
            final CommandResponse response,
            final Exception exception) {
        super(platform, response, exception);
    }

    // ------------------------------------------------------------------------

    private Iterator<IEvent> _expectSequenceOfEvents(final IEvent... expectedEvents) {
        if (expectedEvents.length != platform().recordedEvents.size()) {
            reporter().reportWrongEvent(
                    platform().recordedEvents,
                    Arrays.asList(expectedEvents),
                    exception()
            );
        }

        final Iterator<IEvent> iterator = platform().recordedEvents.iterator();
        for (final IEvent expectedEvent : expectedEvents) {
            final IEvent actualEvent = iterator.next();
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
    public KasperPlatformCommandResultValidator expectSequenceOfEvents(final IEvent... expectedEvents) {
        _expectSequenceOfEvents(expectedEvents);
        return this;
    }

    @Override
    public KasperPlatformCommandResultValidator expectExactSequenceOfEvents(final IEvent... expectedEvents) {
        final Iterator<IEvent> iterator = _expectSequenceOfEvents(expectedEvents);
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
    public KasperFixtureCommandResultValidator expectReturnOK() {
        if ((null == response()) ||  ! OK.equals(((CommandResponse) response()).getStatus())) {
            throw new AxonAssertionError("Command did not answered OK");
        }
        return this;
    }

    public KasperFixtureCommandResultValidator expectReturnError() {
        if ((null == response()) ||  ! ERROR.equals(((CommandResponse) response()).getStatus())) {
            throw new AxonAssertionError("Command did not answered an ERROR");
        }
        return this;
    }

    public KasperFixtureCommandResultValidator expectReturnRefused() {
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
        if ((null == response())
                || (null == ((CommandResponse) response()).getReason())
                || ! ERROR.equals(((CommandResponse) response()).getStatus())
                || ! ((CommandResponse) response()).getReason().getCode().contentEquals(code.toString())) {
            throw new AxonAssertionError(
                    "Command did not answered the expected error code"
            );
        }
        return this;
    }

    public KasperFixtureCommandResultValidator expectReturnRefused(final CoreReasonCode code) {
        if ((null == response())
                || (null == ((CommandResponse) response()).getReason())
                || ! REFUSED.equals(((CommandResponse) response()).getStatus())
                || ! ((CommandResponse) response()).getReason().getCode().contentEquals(code.toString())) {
            throw new AxonAssertionError(
                    "Command did not answered the expected error code"
            );
        }
        return this;
    }

}
