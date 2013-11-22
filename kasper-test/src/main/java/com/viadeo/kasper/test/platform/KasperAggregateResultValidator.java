// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.event.IEvent;
import org.axonframework.test.AxonAssertionError;
import org.axonframework.test.ResultValidator;
import org.hamcrest.Matcher;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.test.matchers.KasperMatcher.equalTo;
import static org.axonframework.test.matchers.Matchers.*;

public class KasperAggregateResultValidator
        implements KasperFixtureResultValidator, KasperFixtureCommandResultValidator {

    private final ResultValidator validator;
    private final KasperReason validationReason;

    // ------------------------------------------------------------------------

    KasperAggregateResultValidator(final ResultValidator validator) {
        this.validator = checkNotNull(validator);
        this.validationReason = null;
    }

    KasperAggregateResultValidator(final KasperReason validationReason) {
        this.validationReason = validationReason;
        this.validator = null;
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperAggregateResultValidator expectSequenceOfEvents(final IEvent... events) {
        checkValidation();

        final Matcher[] matchers = new Matcher[events.length];

        for (int i = 0 ; i < events.length ; i++) {
            matchers[i] = equalTo(events[i]);
        }

        validator.expectEventsMatching(payloadsMatching(exactSequenceOf(matchers)));

        return this;
    }

    @Override
    public KasperAggregateResultValidator expectExactSequenceOfEvents(final IEvent... events) {
        checkValidation();;

        final Matcher[] matchers = new Matcher[events.length + 1];

        int i;
        for (i = 0 ; i < events.length ; i++) {
            matchers[i] = equalTo(events[i]);
        }

        matchers[i] = andNoMore();

        validator.expectEventsMatching(payloadsMatching(exactSequenceOf(matchers)));

        return this;
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperAggregateResultValidator expectReturnResponse(final CommandResponse commandResponse) {
        checkValidation();
        validator.expectReturnValue(commandResponse);
        return this;
    }

    @Override
    public KasperAggregateResultValidator expectReturnOK() {
        checkValidation();
        validator.expectReturnValue(CommandResponse.ok());
        return this;
    }

    @Override
    public KasperAggregateResultValidator expectException(final Class<? extends Throwable> expectedException) {
        checkValidation();
        validator.expectException(expectedException);
        return this;
    }

    @Override
    public KasperAggregateResultValidator expectException(final Matcher<?> matcher) {
        checkValidation();
        validator.expectException(matcher);
        return this;
    }

    @Override
    public KasperFixtureResultValidator expectValidationErrorOnField(final String field) {
        if (null == validationReason) {
            throw new AxonAssertionError(String.format(
                    "The expected validation error on field %s not occured",
                    field
            ));
        }

        final Collection<String> messages = validationReason.getMessages();
        boolean found = false;
        for (final String message : messages) {
            if (message.startsWith("VALIDATION")) {
                final String[] parts = message.split(":");
                if (parts.length > 2) {
                    if (parts[1].contentEquals(field)) {
                        found = true;
                    }
                }
            }
        }

        if ( ! found) {
            throw new AxonAssertionError(String.format(
                    "The expected validation error on field %s not occured",
                    field
            ));
        }

        return this;
    }

    // ------------------------------------------------------------------------

    private void checkValidation() {
        if (null != validationReason) {
            throw new AxonAssertionError("Error on validation : " + validationReason.toString());
        }
    }

}