// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.event.IEvent;
import org.axonframework.test.ResultValidator;
import org.hamcrest.Matcher;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.test.event.EventMatcher.equalToEvent;
import static org.axonframework.test.matchers.Matchers.*;

public class KasperAggregateResultValidator
        implements KasperFixtureResultValidator, KasperFixtureCommandResultValidator {

    private final ResultValidator validator;

    // ------------------------------------------------------------------------

    KasperAggregateResultValidator(final ResultValidator validator) {
        this.validator = checkNotNull(validator);
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperAggregateResultValidator expectSequenceOfEvents(final IEvent... events) {
        final Matcher[] matchers = new Matcher[events.length];

        for (int i = 0 ; i < events.length ; i++) {
            matchers[i] = equalToEvent(events[i]);
        }

        validator.expectEventsMatching(payloadsMatching(exactSequenceOf(matchers)));

        return this;
    }

    @Override
    public KasperAggregateResultValidator expectExactSequenceOfEvents(final IEvent... events) {
        final Matcher[] matchers = new Matcher[events.length + 1];

        int i;
        for (i = 0 ; i < events.length ; i++) {
            matchers[i] = equalToEvent(events[i]);
        }

        matchers[i] = andNoMore();

        validator.expectEventsMatching(payloadsMatching(exactSequenceOf(matchers)));

        return this;
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperAggregateResultValidator expectReturnResponse(final CommandResponse commandResponse) {
        validator.expectReturnValue(commandResponse);
        return this;
    }

    @Override
    public KasperAggregateResultValidator expectReturnOK() {
        validator.expectReturnValue(CommandResponse.ok());
        return this;
    }

    @Override
    public KasperAggregateResultValidator expectException(final Class<? extends Throwable> expectedException) {
        validator.expectException(expectedException);
        return this;
    }

    @Override
    public KasperAggregateResultValidator expectException(final Matcher<?> matcher) {
        validator.expectException(matcher);
        return this;
    }

}
