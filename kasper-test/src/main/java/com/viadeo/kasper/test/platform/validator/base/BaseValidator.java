// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform.validator.base;

import org.axonframework.test.Reporter;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.test.platform.KasperPlatformFixture.RecordingPlatform;

public abstract class BaseValidator {

    private final RecordingPlatform platform;
    private final Object response;
    private final Exception exception;
    private final Reporter reporter;

    // ------------------------------------------------------------------------

    public BaseValidator(final RecordingPlatform platform, final Object response, final Exception exception) {
        this.platform = checkNotNull(platform);
        this.response = response;
        this.exception = exception;
        this.reporter = new Reporter();
    }

    // ------------------------------------------------------------------------

    protected Reporter reporter() {
        return reporter;
    }

    protected RecordingPlatform platform() {
        return this.platform;
    }

    protected Exception exception() {
        return this.exception;
    }

    protected Object response() {
        return this.response;
    }

    protected boolean hasException() {
        return this.exception != null;
    }

}
