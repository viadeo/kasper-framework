// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.google.common.base.Objects;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.annotation.Immutable;
import com.viadeo.kasper.cqrs.TransportMode;
import com.viadeo.kasper.cqrs.query.http.HTTPQueryResponse;
import com.viadeo.kasper.exception.KasperException;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Data Transfer Object
 *
 * Represents an anemic transfer entity, in the Query semantics
 *
 * Can be used to store some properties of a root entity which can be later the
 * base entity of a Kasper CQRS domain entity command.
 */
public class QueryResponse<RESULT extends QueryResult> implements Serializable, Immutable {
    private static final long serialVersionUID = -6543664128786160837L;

    /**
     * Accepted values for query response statuses
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
    private final RESULT result;
    private final KasperReason reason;

    // ------------------------------------------------------------------------

    public static <R extends QueryResult> QueryResponse<R> error(final KasperReason reason) {
        return new QueryResponse<R>(checkNotNull(reason));
    }

    public static <R extends QueryResult> QueryResponse<R> refused(final KasperReason reason) {
        return new QueryResponse<R>(Status.REFUSED, checkNotNull(reason));
    }

    public static <R extends QueryResult> QueryResponse<R> of(final R result) {
        return new QueryResponse<R>(checkNotNull(result));
    }

    // ------------------------------------------------------------------------

    public QueryResponse(final QueryResponse<RESULT> response) {
        this.status = Status.OK;
        this.result = response.result;
        this.reason = response.reason;
    }

    public QueryResponse(final RESULT result) {
        this.status = Status.OK;
        this.result = checkNotNull(result);
        this.reason = null;
    }
    
    public QueryResponse(final KasperReason reason) {
        this.status= Status.ERROR;
        this.result = null;
        this.reason = checkNotNull(reason);
    }

    public QueryResponse(final Status status, final KasperReason reason) {
        this.status = status;
        this.result = null;
        this.reason = checkNotNull(reason);
    }

    // ------------------------------------------------------------------------

    public Status getStatus() {
        return this.status;
    }

    public KasperReason getReason() {
        return reason;
    }
    
    public RESULT getResult() {
        return result;
    }
    
    public boolean isOK() {
        return null == reason;
    }

    // ------------------------------------------------------------------------

    public TransportMode getTransportMode() {
         if (HTTPQueryResponse.class.isAssignableFrom(this.getClass())) {
             return TransportMode.HTTP;
        }
        return TransportMode.UNKNOWN;
    }

    public HTTPQueryResponse asHttp() {
        if (HTTPQueryResponse.class.isAssignableFrom(this.getClass())) {
            return (HTTPQueryResponse) this;
        }
        throw new KasperException("Not an HTTP query response");
    }

    // ------------------------------------------------------------------------

    @Override
    public int hashCode() {
        return Objects.hashCode(status) + Objects.hashCode(result) + Objects.hashCode(reason);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == checkNotNull(obj)) {
            return true;
        }
        if ( ! getClass().equals(obj.getClass())) {
            return false;
        }

        final QueryResponse other = (QueryResponse) obj;
        return Objects.equal(other.status, this.status)
                && Objects.equal(other.result, this.result)
                && Objects.equal(other.reason, this.reason);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("status", this.status)
                .add("result", this.result)
                .add("reason", this.reason)
                .toString();
    }

}
