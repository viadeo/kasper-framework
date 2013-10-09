// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.CoreErrorCode;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.event.ErrorEvent;

import static com.google.common.base.Preconditions.checkNotNull;

public class AbstractErrorEvent extends AbstractEvent implements ErrorEvent {

    private final String code;
    private final Exception exception;
    private final String message;

    // ------------------------------------------------------------------------

    public AbstractErrorEvent(final Context context, final String code,
                              final String message, final Exception exception) {
        super(context);
        this.code = checkNotNull(code);
        this.exception = checkNotNull(exception);
        this.message = checkNotNull(message);
    }

    public AbstractErrorEvent(final Context context, final String code, final Exception exception) {
        super(context);
        this.code = checkNotNull(code);
        this.exception = checkNotNull(exception);
        this.message = null;
    }

    public AbstractErrorEvent(final Context context, final String code, final String message) {
        super(context);
        this.code = checkNotNull(code);
        this.exception = null;
        this.message = checkNotNull(message);
    }

    public AbstractErrorEvent(final Context context, final String message) {
        super(context);
        this.code = CoreErrorCode.UNKNOWN_ERROR.toString();
        this.exception = null;
        this.message = checkNotNull(message);
    }

    public AbstractErrorEvent(final Context context, final Exception exception) {
        super(context);
        this.code = CoreErrorCode.UNKNOWN_ERROR.toString();
        this.exception = checkNotNull(exception);
        this.message = null;
    }

    public AbstractErrorEvent(final Context context, final CoreErrorCode code,
                              final String message, final Exception exception) {
         this(context, code.toString(), message, exception);
    }

    public AbstractErrorEvent(final Context context, final CoreErrorCode code, final Exception exception) {
        this(context, code.toString(), exception);
    }

    public AbstractErrorEvent(final Context context, final CoreErrorCode code, final String message) {
        this(context, code.toString(), message);
    }

    // ------------------------------------------------------------------------

    @Override
    public Optional<Exception> getException() {
        return Optional.fromNullable(this.exception);
    }

    @Override
    public Optional<String> getMessage() {
        if (null != this.message) {
            return Optional.of(this.message);
        }
        if (null != this.exception) {
            return Optional.fromNullable(this.exception.getMessage());
        }
        return Optional.absent();
    }

    @Override
    public Optional<String> getCode() {
        return Optional.fromNullable(this.code);
    }

}
