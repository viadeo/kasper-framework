// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.exceptions;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.viadeo.kasper.KasperError;

/**
 * The base Kasper query exception
 */
public class KasperQueryException extends RuntimeException {

    private static final long serialVersionUID = 4429295125026389937L;
    private final List<KasperError> errors;

    // ------------------------------------------------------------------------

    public static class ExceptionBuilder {
        private final List<KasperError> errors = new ArrayList<>();
        private String message;
        private Throwable exception;

        public ExceptionBuilder(final String message) {
            this.message = message;
        }

        public ExceptionBuilder addError(final String code, final String message) {
            return addError(new KasperError(code, message));
        }

        public ExceptionBuilder addError(final String code, final String message, final String userMessage) {
            return addError(new KasperError(code, message, userMessage));
        }

        public ExceptionBuilder addError(final KasperError... error) {
            for (KasperError e : error)
                errors.add(e);
            return this;
        }

        public ExceptionBuilder addErrors(final List<KasperError> errors) {
            if (null != errors) {
                for (final KasperError error : errors) {
                    addError(error);
                }
            }
            return this;
        }

        public ExceptionBuilder reason(final Throwable t) {
            this.exception = t;

            /* FIXME lets add the reason for which the exception was thrown to the error list 
             * (in case the actual message swallows some information)
             *  better to provide more information than not enough
             */
            if (null != t.getMessage()) {
                addError(KasperError.UNKNOWN_ERROR, t.getMessage());
            }
            return this;
        }

        public KasperQueryException create() {
            return new KasperQueryException(message, exception, errors);
        }

        public void throwEx() {
            final KasperQueryException ex = create();

            // pop first element in the stack as it will be this method, we want clean exceptions.
            final StackTraceElement[] stackTrace = ex.getStackTrace();
            final StackTraceElement[] newStackTrace = new StackTraceElement[stackTrace.length - 1];

            System.arraycopy(stackTrace, 1, newStackTrace, 0, stackTrace.length - 1);
            ex.setStackTrace(newStackTrace);

            throw ex;
        }
    }

    // ------------------------------------------------------------------------

    public static ExceptionBuilder exception(final String message) {
        return new ExceptionBuilder(message);
    }

    public static ExceptionBuilder exception(final Throwable exception) {
        return new ExceptionBuilder(exception.getMessage()).reason(exception);
    }

    // ------------------------------------------------------------------------

    public KasperQueryException(final String message) {
        this(Preconditions.checkNotNull(message), null, null);
    }

    public KasperQueryException(final String message, final Throwable t) {
        this(message, t, null);
    }

    public KasperQueryException(final Throwable t) {
        super(t);
        this.errors = null;
    }

    public KasperQueryException(final Throwable t, final List<KasperError> errors) {
        super(t);
        this.errors = errors;
    }

    public KasperQueryException(final String message, final Throwable t, final List<KasperError> errors) {
        super(message, t);
        this.errors = errors;
    }

    // ------------------------------------------------------------------------

    public Optional<List<KasperError>> getErrors() {
        if (null != errors) {
            return Optional.<List<KasperError>> of(ImmutableList.copyOf(errors));
        } else {
            return Optional.absent();
        }
    }

}
