// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.event.IEvent;
import org.axonframework.test.Reporter;
import org.axonframework.test.matchers.EqualFieldsMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import java.util.Arrays;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.test.event.EventMatcher.equalToEvent;
import static org.axonframework.test.matchers.Matchers.equalTo;

public class KasperPlatformResultValidator
        implements KasperFixtureResultValidator, KasperFixtureCommandResultValidator {

    private final KasperPlatformFixture.RecordingPlatform platform;

    private final CommandResponse response;
    private final Exception exception;

    private final Reporter reporter = new Reporter();

    // ------------------------------------------------------------------------

    KasperPlatformResultValidator(
            final KasperPlatformFixture.RecordingPlatform platform,
            final CommandResponse response,
            final Exception exception) {
        this.platform = checkNotNull(platform);
        this.response = response;
        this.exception = exception;
    }

    // ------------------------------------------------------------------------

    private Iterator<IEvent> _expectSequenceOfEvents(final IEvent... expectedEvents) {
        if (expectedEvents.length != platform.recordedEvents.size()) {
            reporter.reportWrongEvent(
                    platform.recordedEvents,
                    Arrays.asList(expectedEvents),
                    this.exception
            );
        }

        final Iterator<IEvent> iterator = platform.recordedEvents.iterator();
        for (final IEvent expectedEvent : expectedEvents) {
            final IEvent actualEvent = iterator.next();
            if ( ! equalToEvent(expectedEvent).matches(actualEvent)) {
                reporter.reportWrongEvent(
                        platform.recordedEvents,
                        Arrays.asList(expectedEvents),
                        this.exception
                );
            }
        }

        return iterator;
    }

    @Override
    public KasperPlatformResultValidator expectSequenceOfEvents(final IEvent... expectedEvents) {
        _expectSequenceOfEvents(expectedEvents);
        return this;
    }

    @Override
    public KasperPlatformResultValidator expectExactSequenceOfEvents(final IEvent... expectedEvents) {
        final Iterator<IEvent> iterator = _expectSequenceOfEvents(expectedEvents);
        if (iterator.hasNext()) {
            reporter.reportWrongEvent(
                    platform.recordedEvents,
                    Arrays.asList(expectedEvents),
                    this.exception
            );
        }
        return this;
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperPlatformResultValidator expectReturnResponse(final CommandResponse commandResponse) {
        expectReturnValue(equalTo(commandResponse));
        return this;
    }

    @Override
    public KasperPlatformResultValidator expectReturnOK() {
        return this.expectReturnResponse(CommandResponse.ok());
    }

    @Override
    public KasperPlatformResultValidator expectException(final Class<? extends Throwable> expectedException) {
        return this.expectException(equalTo(expectedException));
    }

    @Override
    public KasperPlatformResultValidator expectException(final Matcher<?> matcher) {
        final StringDescription description = new StringDescription();
        matcher.describeTo(description);
        if (null == this.exception) {
            reporter.reportUnexpectedReturnValue(this.response, description);
        }
        if ( ! matcher.matches(this.exception)) {
            reporter.reportWrongException(this.exception, description);
        }
        return this;
    }

    // ------------------------------------------------------------------------

    private void expectReturnValue(final Matcher<?> matcher) {

        final StringDescription description = new StringDescription();
        matcher.describeTo(description);

        if (null != this.exception) {
            reporter.reportUnexpectedException(this.exception, description);
        } else if ( ! matcher.matches(this.response)) {
            reporter.reportWrongResult(this.response, description);
        }

    }

    private boolean verifyEventEquality(final Object expectedEvent, final Object actualEvent) {
        if ( ! expectedEvent.getClass().equals(actualEvent.getClass())) {
            return false;
        }

        final EqualFieldsMatcher<Object> matcher = new EqualFieldsMatcher<>(expectedEvent);
        if (!matcher.matches(actualEvent)) {
            reporter.reportDifferentEventContents(expectedEvent.getClass(),
                    matcher.getFailedField(),
                    matcher.getFailedFieldActualValue(),
                    matcher.getFailedFieldExpectedValue());
        }

        return true;
    }

}
