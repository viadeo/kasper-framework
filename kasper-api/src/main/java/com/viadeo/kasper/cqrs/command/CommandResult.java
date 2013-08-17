// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.annotation.Immutable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Base Kasper command result implementation
 */
public class CommandResult implements Serializable, Immutable {

    /**
     * Accepted values for command result statuses
     */
    public static enum Status {
        OK,         /** All is ok */
        REFUSED,    /** Refused by some intermediate validation mechanisms */
        ERROR       /** Just error in command handling or domain business */
    }

    /**
     * The current command status
     */
    private final Status status;
    private final List<KasperError> errors;

    // ------------------------------------------------------------------------

    public static class Builder {
        private final List<KasperError> errors = new ArrayList<KasperError>();
        private Status status = Status.OK;

        public Builder addError(final String code, final String message) {
            return addError(new KasperError(code, message));
        }

        public Builder addError(final String code, final String message, final String userMessage) {
            return addError(new KasperError(code, message, userMessage));
        }

        public Builder addError(final KasperError... errors) {
            if (status == Status.OK) {
                status = Status.ERROR;
            }
            for (final KasperError error : errors) {
                this.errors.add(error);
            }
            return this;
        }

        public Builder addErrors(final List<KasperError> errors) {
            for (final KasperError error : errors){
                addError(error);
            }
            return this;
        }

        public Builder status(final Status status) {
            this.status = status;
            return this;
        }

        public boolean isError() {
            return Status.OK != status;
        }

        public CommandResult build() {
            return new CommandResult(status, errors);
        }

    }

    // ------------------------------------------------------------------------

    public static Builder error() {
        return new Builder().status(Status.ERROR);
    }

    public static Builder refused() {
        return new Builder().status(Status.REFUSED);
    }

    public static CommandResult ok() {
        return new Builder().status(Status.OK).build();
    }


    // ------------------------------------------------------------------------

    private CommandResult(final Status status, final List<KasperError> errors) {
        this.status = Preconditions.checkNotNull(status);
        if (null != errors) {
            this.errors = errors;
        } else {
            this.errors = ImmutableList.of();
        }
    }

    // ------------------------------------------------------------------------

    /**
     * @return the current command result execution status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @return a list of errors or empty if command succeeded.
     */
    public Optional<List<KasperError>> getErrors() {
        return Optional.<List<KasperError>> fromNullable(ImmutableList.copyOf(errors));
    }

    /**
     * @return true if this command has resulted to an error
     */
    public boolean isError() {
        return this.status.equals(Status.ERROR);
    }

}
