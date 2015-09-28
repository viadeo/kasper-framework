// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform.validator.base;

import org.axonframework.commandhandling.interceptors.JSR303ViolationException;
import org.axonframework.test.AxonAssertionError;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import javax.validation.ConstraintViolation;

import static com.viadeo.kasper.test.platform.KasperMatcher.equalTo;
import static com.viadeo.kasper.test.platform.KasperPlatformFixture.RecordingPlatform;

public class DefaultBaseValidator extends BaseValidator
        implements ExceptionValidator<DefaultBaseValidator>, FieldValidator<DefaultBaseValidator> {

    public DefaultBaseValidator(final RecordingPlatform platform, final Object response, final Exception exception) {
        super(platform, response, exception);
    }

    // ------------------------------------------------------------------------

    @Override
    public DefaultBaseValidator expectException(final Class<? extends Throwable> expectedException) {
        return this.expectException(equalTo(expectedException));
    }

    @Override
    public DefaultBaseValidator expectException(final Matcher<?> matcher) {
        final StringDescription description = new StringDescription();
        matcher.describeTo(description);
        if ( ! hasException()) {
            reporter().reportUnexpectedReturnValue(this.response(), description);
        }
        if ( ! matcher.matches(this.exception())) {
            reporter().reportWrongException(this.exception(), description);
        }
        return this;
    }

    @Override
    public DefaultBaseValidator expectValidationErrorOnField(final String field) {

        if ( ( ! hasException() ) || ( ! JSR303ViolationException.class.equals(this.exception().getClass()))) {
            throw new AxonAssertionError(String.format(
                    "The expected validation error on field %s not occurred",
                    field
            ));
        }

        boolean found = false;

        final JSR303ViolationException jsrException = (JSR303ViolationException) this.exception();

        for (final ConstraintViolation violation : jsrException.getViolations()) {
            if (violation.getPropertyPath().toString().contentEquals(field)) {
                found = true;
            }
        }

        if ( ! found) {
            throw new AxonAssertionError(String.format(
                "The expected validation error on field %s not occurred",
                field
            ));
        }

        return this;
    }

    protected void expectReturnValue(final Matcher<?> matcher) {
        final StringDescription description = new StringDescription();
        matcher.describeTo(description);

        if (null != this.exception()) {
            reporter().reportUnexpectedException(this.exception(), description);
        } else if ( ! matcher.matches(this.response())) {
            reporter().reportWrongResult(this.response(), description);
        }
    }

}
