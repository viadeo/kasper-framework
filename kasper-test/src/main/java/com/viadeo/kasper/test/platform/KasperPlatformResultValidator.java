// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import org.axonframework.test.Reporter;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.axonframework.test.matchers.Matchers.equalTo;

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

}
