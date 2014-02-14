// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper;

import com.viadeo.kasper.annotation.Immutable;
import com.viadeo.kasper.cqrs.TransportMode;
import com.viadeo.kasper.exception.KasperException;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperResponse implements Serializable, Immutable {

    /**
     * Accepted values for command response statuses
     */
    public static enum Status {
        OK,         /** All is ok */
        REFUSED,    /** Refused by some intermediate validation mechanisms */
        ERROR,      /** Error in handling or domain business */
        ACCEPTED    /** The command or the query has been accepted but answer will be made later */
    }

    /**
     * The current command status
     */
    private final Status status;
    private final KasperReason reason;

    // ------------------------------------------------------------------------

    public KasperResponse() {
        this.status = Status.OK;
        this.reason = null;
    }

    public KasperResponse(final KasperResponse response) {
        this(response.status, response.reason);
    }

    public KasperResponse(final Status status, final KasperReason reason) {
        this.status = checkNotNull(status);

        if (!status.equals(Status.OK) && (null == reason)) {
            throw new IllegalStateException("Please provide a reason to the response");
        }

        if (status.equals(Status.OK) && (null != reason)) {
            throw new IllegalStateException("Invalid response OK provided with an reason");
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
        if (HTTPKasperResponse.class.isAssignableFrom(this.getClass())) {
            return TransportMode.HTTP;
        }
        return TransportMode.UNKNOWN;
    }

    public HTTPKasperResponse asHttp(){
        if (TransportMode.HTTP.equals(getTransportMode()) {
            return (HTTPKasperResponse) this;
        }
        throw new KasperException("Not an HTTP response");
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

        final KasperResponse other = (KasperResponse) obj;

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
