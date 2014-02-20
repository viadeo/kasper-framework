// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.google.common.base.Optional;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.annotation.Immutable;
import com.viadeo.kasper.cqrs.TransportMode;
import com.viadeo.kasper.cqrs.command.http.HTTPCommandResponse;
import com.viadeo.kasper.exception.KasperException;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base Kasper command response implementation
 */
public class CommandResponse implements Serializable, Immutable {
    private static final long serialVersionUID = -938831661655150085L;

    /**
     * Accepted values for command response statuses
     */
    public static enum Status {
        OK,         /** All is ok */
        REFUSED,    /** Refused by some intermediate validation mechanisms */
        ERROR       /** Just reason in command handling or domain business */
    }

    /**
     * The current command status
     */
    private final Status status;
    private final KasperReason reason;

    private String securityToken;

    // ------------------------------------------------------------------------

    public static CommandResponse error(final KasperReason reason) {
        return new CommandResponse(Status.ERROR, reason);
    }

    public static CommandResponse error(final String code, final String reason) {
        return error(new KasperReason(code, reason));
    }

    public static CommandResponse error(final CoreReasonCode code, final String reason) {
        return error(new KasperReason(checkNotNull(code), reason));
    }

    public static CommandResponse error(final CoreReasonCode code) {
        return error(new KasperReason(checkNotNull(code)));
    }

    // ------------------------------------------------------------------------

    public static CommandResponse refused(final KasperReason reason) {
        return new CommandResponse(Status.REFUSED, reason);
    }

    public static CommandResponse refused(final String code, final String reason) {
        return refused(new KasperReason(code, reason));
    }

    public static CommandResponse refused(final CoreReasonCode code, final String reason) {
        return refused(new KasperReason(code, reason));
    }

    public static CommandResponse refused(final CoreReasonCode code) {
        return refused(new KasperReason(code));
    }

    // ------------------------------------------------------------------------

    public static CommandResponse ok() {
        return new CommandResponse(Status.OK, null);
    }

    // ------------------------------------------------------------------------

    public CommandResponse withSecurityToken(final String securityToken) {
        this.securityToken = checkNotNull(securityToken);
        return this;
    }

    public Optional<String> getSecurityToken() {
        return Optional.fromNullable(this.securityToken);
    }

    // ------------------------------------------------------------------------

    public CommandResponse(final CommandResponse response) {
        this.status = response.status;
        this.reason = response.reason;

        if (response.getSecurityToken().isPresent()) {
            this.securityToken = response.getSecurityToken().get();
        }
    }

    public CommandResponse(final Status status, final KasperReason reason) {
        this.status = checkNotNull(status);
        
        if (!status.equals(Status.OK) && (null == reason)) {
            throw new IllegalStateException("Please provide a reason to the command response");
        }

        if (status.equals(Status.OK) && (null != reason)) {
            throw new IllegalStateException("Invalid command response OK provided with an reason");
        }
        
        this.reason = reason;
    }

    // ------------------------------------------------------------------------

    /**
     * @return the current command response execution status
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     * @return true if the current status is OK
     */
    public boolean isOK() {
        return (this.status.equals(Status.OK));
    }

    /**
     * @return a list of reasons or empty if command succeeded.
     */
    public KasperReason getReason() {
        return reason;
    }

    /**
     * @return true if this command has answered with a reason
     */
    public boolean hasReason() {
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
        throw new KasperException("Not an HTTP command response");
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final CommandResponse other = (CommandResponse) obj;

        return com.google.common.base.Objects.equal(this.status, other.status)
               && com.google.common.base.Objects.equal(this.reason, other.reason);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(status, reason);
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .addValue(this.status)
                .addValue(this.reason)
                .toString();
    }

}
