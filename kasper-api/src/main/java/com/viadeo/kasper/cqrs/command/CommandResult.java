// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.viadeo.kasper.KasperError;

/**
 * Base Kasper command result implementation
 */
public class CommandResult {

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

    public static class ResultBuilder {
        private final List<KasperError> errors = new ArrayList<>();
        private Status status = Status.OK;

        public ResultBuilder addError(final String code, final String message) {
            return addError(new KasperError(code, message));
        }

        public ResultBuilder addError(final String code, final String message, final String userMessage) {
            return addError(new KasperError(code, message, userMessage));
        }

        public ResultBuilder addError(final KasperError... errors) {
            if (status == Status.OK) {
                status = Status.ERROR;
            }
            for (final KasperError error : errors) {
                this.errors.add(error);
            }
            return this;
        }

        public ResultBuilder addErrors(final List<KasperError> errors) {
            for (final KasperError error : errors){
                addError(error);
            }
            return this;
        }

        public ResultBuilder status(final Status status) {
            this.status = status;
            return this;
        }

        public boolean isError() {
            return Status.OK != status;
        }

        public CommandResult create() {
            return new CommandResult(status, errors);
        }

    }

    // ------------------------------------------------------------------------

    public static ResultBuilder error() {
        return new ResultBuilder().status(Status.ERROR);
    }

    public static ResultBuilder refused() {
        return new ResultBuilder().status(Status.REFUSED);
    }

    public static CommandResult ok() {
        return new ResultBuilder().status(Status.OK).create();
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
