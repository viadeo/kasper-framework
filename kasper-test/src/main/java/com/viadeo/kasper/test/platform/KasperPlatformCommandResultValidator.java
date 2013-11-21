// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.event.IEvent;

import java.util.Arrays;
import java.util.Iterator;

import static com.viadeo.kasper.test.event.EventMatcher.equalToEvent;
import static org.axonframework.test.matchers.Matchers.equalTo;

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
            if ( ! equalToEvent(expectedEvent).matches(actualEvent)) {
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
    public KasperPlatformCommandResultValidator expectReturnOK() {
        return this.expectReturnResponse(CommandResponse.ok());
    }

}
