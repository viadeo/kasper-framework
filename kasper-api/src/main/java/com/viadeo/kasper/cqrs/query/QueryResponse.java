// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

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

    private final RESULT result;
    private final KasperReason reason;

    // ------------------------------------------------------------------------

    public static <P extends QueryResult> QueryResponse<P> of(final KasperReason reason) {
        return new QueryResponse<P>(reason);
    }

    public static <P extends QueryResult> QueryResponse<P> of(final P response) {
        return new QueryResponse<P>(response);
    }

    // ------------------------------------------------------------------------

    public QueryResponse(final QueryResponse<RESULT> response) {
        this.result = response.result;
        this.reason = response.reason;
    }

    public QueryResponse(final RESULT result) {
        this.result = checkNotNull(result);
        this.reason = null;
    }
    
    public QueryResponse(final KasperReason reason) {
        this.result = null;
        this.reason = checkNotNull(reason);
    }

    // ------------------------------------------------------------------------

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

}
