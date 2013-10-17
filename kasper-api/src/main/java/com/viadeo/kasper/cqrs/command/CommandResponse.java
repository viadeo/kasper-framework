// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.viadeo.kasper.CoreErrorCode;
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.annotation.Immutable;
import com.viadeo.kasper.cqrs.TransportMode;
import com.viadeo.kasper.cqrs.command.http.HTTPCommandResponse;
import com.viadeo.kasper.exception.KasperException;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base Kasper command result implementation
 */
public class CommandResponse implements Serializable, Immutable {
    private static final long serialVersionUID = -938831661655150085L;

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
    private final KasperError error;

    // ------------------------------------------------------------------------

    public static CommandResponse error(final KasperError error) {
        return new CommandResponse(Status.ERROR, error);
    }

    public static CommandResponse error(final String code, final String message) {
        return error(new KasperError(code, message));
    }

    public static CommandResponse error(final CoreErrorCode code, final String message) {
        return error(new KasperError(checkNotNull(code).toString(), message));
    }

    // ------------------------------------------------------------------------

    public static CommandResponse refused(final KasperError error) {
        return new CommandResponse(Status.REFUSED, error);
    }

    public static CommandResponse refused(final String code, final String message) {
        return refused(new KasperError(code, message));
    }

    public static CommandResponse refused(final CoreErrorCode code, final String message) {
        return refused(new KasperError(code, message));
    }

    // ------------------------------------------------------------------------

    public static CommandResponse ok() {
        return new CommandResponse(Status.OK, null);
    }

    // ------------------------------------------------------------------------

    public CommandResponse(final CommandResponse result) {
        this.status = result.status;
        this.error = result.error;
    }

    public CommandResponse(final Status status, final KasperError error) {
        this.status = checkNotNull(status);
        
        if (!status.equals(Status.OK) && (null == error)) {
            throw new IllegalStateException("Please provide an error to the command result");
        }

        if (status.equals(Status.OK) && (null != error)) {
            throw new IllegalStateException("Invalid command result OK provided with an error");
        }
        
        this.error = error;
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
    public KasperError getError() {
        return error;
    }

    /**
     * @return true if this command has resulted to an error
     */
    public boolean isError() {
        return this.status != Status.OK;
    }

    // ------------------------------------------------------------------------

    public TransportMode getTransportMode() {
        if (HTTPCommandResponse.class.isAssignableFrom(this.getClass())) {
            return TransportMode.HTTP;
        }
        return TransportMode.UNKNOWN;
    }

    public HTTPCommandResponse asHttp(){
        if (HTTPCommandResponse.class.isAssignableFrom(this.getClass())) {
            return (HTTPCommandResponse) this;
        }
        throw new KasperException("Not an HTTP command result");
    }

}
