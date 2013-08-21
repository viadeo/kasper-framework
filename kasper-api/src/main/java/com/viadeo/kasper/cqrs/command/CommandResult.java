// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import static com.google.common.base.Preconditions.*;
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.annotation.Immutable;

import java.io.Serializable;

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

    private static final long serialVersionUID = -938831661655150085L;

    /**
     * The current command status
     */
    private final Status status;
    private final KasperError error;
    
    // ------------------------------------------------------------------------

    public static CommandResult error(KasperError error) {
        return new CommandResult(Status.ERROR, error);
    }

    public static CommandResult refused(KasperError error) {
        return new CommandResult(Status.REFUSED, error);
    }

    public static CommandResult ok() {
        return new CommandResult(Status.OK, null);
    }

    // ------------------------------------------------------------------------
    
    public CommandResult(final Status status, final KasperError error) {
        this.status = checkNotNull(status);
        
        if (status != Status.OK && error == null) throw new IllegalStateException("status != Status.OK && error == null");
        if (status == Status.OK && error != null) throw new IllegalStateException("status == Status.OK && error != null");
        
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

}
