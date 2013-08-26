// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.annotation.Immutable;

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
public class QueryResult<PAYLOAD extends QueryPayload> implements Serializable, Immutable {
    private static final long serialVersionUID = -6543664128786160837L;

    private final PAYLOAD payload;
    private final KasperError error;

    // ------------------------------------------------------------------------

    public static <P extends QueryPayload> QueryResult<P> of(final KasperError error) {
        return new QueryResult<P>(error);
    }

    public static <P extends QueryPayload> QueryResult<P> of(final P result) {
        return new QueryResult<P>(result);
    }

    // ------------------------------------------------------------------------

    public QueryResult(final PAYLOAD payload) {
        this.payload = checkNotNull(payload);
        this.error = null;
    }
    
    public QueryResult(final KasperError error) {
        this.payload = null;
        this.error = checkNotNull(error);
    }

    // ------------------------------------------------------------------------

    public KasperError getError() {
        return error;
    }
    
    public PAYLOAD getPayload() {
        return payload;
    }
    
    public boolean isError() {
        return error != null;
    }

}
