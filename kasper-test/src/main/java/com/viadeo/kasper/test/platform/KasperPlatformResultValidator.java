// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import org.axonframework.commandhandling.interceptors.JSR303ViolationException;
import org.axonframework.test.AxonAssertionError;
import org.axonframework.test.Reporter;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import javax.validation.ConstraintViolation;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.test.matchers.KasperMatcher.equalTo;

public abstract class KasperPlatformResultValidator
        implements KasperFixtureResultValidator {

    private final KasperPlatformFixture.RecordingPlatform platform;

    private final Object response;
    private final Exception exception;

    private final Reporter reporter = new Reporter();

    // ------------------------------------------------------------------------

    protected KasperPlatformResultValidator(
            final KasperPlatformFixture.RecordingPlatform platform,
            final Object response,
            final Exception exception) {
        this.platform = checkNotNull(platform);
        this.response = response;
        this.exception = exception;
    }

    protected Reporter reporter() {
        return reporter;
    }

    protected KasperPlatformFixture.RecordingPlatform platform() {
        return this.platform;
    }

    protected Exception exception() {
        return this.exception;
    }

    protected Object response() {
        return this.response;
    }

    // ------------------------------------------------------------------------

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


    protected void expectReturnValue(final Matcher<?> matcher) {

        final StringDescription description = new StringDescription();
        matcher.describeTo(description);

        if (null != this.exception) {
            reporter.reportUnexpectedException(this.exception, description);
        } else if ( ! matcher.matches(this.response)) {
            reporter.reportWrongResult(this.response, description);
        }

    }

    @Override
    public KasperFixtureResultValidator expectValidationErrorOnField(final String field) {

        if ((null == exception()) || ( ! JSR303ViolationException.class.equals(exception().getClass()))) {
            throw new AxonAssertionError(String.format(
                    "The expected validation error on field %s not occured",
                    field
            ));
        }

        boolean found = false;

        final JSR303ViolationException jsrException = (JSR303ViolationException) exception();

        for (final ConstraintViolation violation : jsrException.getViolations()) {
            if (violation.getPropertyPath().toString().contentEquals(field)) {
                found = true;
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

}
