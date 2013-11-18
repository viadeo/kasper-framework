// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.event.Event;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class ErrorEvent extends Event {

    private final String code;
    private final Exception exception;
    private final String message;

    // ------------------------------------------------------------------------

    public ErrorEvent(final String code,
                      final String message,
                      final Exception exception) {
        this.code = checkNotNull(code);
        this.exception = checkNotNull(exception);
        this.message = checkNotNull(message);
    }

    public ErrorEvent(final String code, final Exception exception) {
        this.code = checkNotNull(code);
        this.exception = checkNotNull(exception);
        this.message = null;
    }

    public ErrorEvent(final String code, final String message) {
        this.code = checkNotNull(code);
        this.exception = null;
        this.message = checkNotNull(message);
    }

    public ErrorEvent(final String message) {
        this.code = CoreReasonCode.UNKNOWN_REASON.toString();
        this.exception = null;
        this.message = checkNotNull(message);
    }

    public ErrorEvent(final Exception exception) {
        this.code = CoreReasonCode.UNKNOWN_REASON.toString();
        this.exception = checkNotNull(exception);
        this.message = null;
    }

    public ErrorEvent(final CoreReasonCode code,
                      final String message, final Exception exception) {
         this(code.toString(), message, exception);
    }

    public ErrorEvent(final CoreReasonCode code, final Exception exception) {
        this(code.toString(), exception);
    }

    public ErrorEvent(final CoreReasonCode code, final String message) {
        this(code.toString(), message);
    }

    // ------------------------------------------------------------------------

    public Optional<Exception> getException() {
        return Optional.fromNullable(this.exception);
    }

    public Optional<String> getMessage() {
        if (null != this.message) {
            return Optional.of(this.message);
        }
        if (null != this.exception) {
            return Optional.fromNullable(this.exception.getMessage());
        }
        return Optional.absent();
    }

    public Optional<String> getCode() {
        return Optional.fromNullable(this.code);
    }

    // ------------------------------------------------------------------------

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), this.code, this.exception, this.message);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == checkNotNull(obj)) {
            return true;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        final ErrorEvent other = (ErrorEvent) obj;

        return super.equals(obj) &&
                Objects.equal(this.code, other.code) &&
                Objects.equal(this.exception, other.exception) &&
                Objects.equal(this.message, other.message);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(super.toString())
                .addValue(this.code)
                .addValue(this.exception)
                .addValue(this.message)
                .toString();
    }

}
