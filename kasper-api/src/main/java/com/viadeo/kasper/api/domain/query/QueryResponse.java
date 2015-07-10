// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.domain.query;

import com.google.common.base.Objects;
import com.viadeo.kasper.api.domain.response.CoreReasonCode;
import com.viadeo.kasper.api.domain.response.KasperReason;
import com.viadeo.kasper.api.domain.response.KasperResponse;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Data Transfer Object
 *
 * Represents an anemic transfer entity, in the Query semantics
 *
 * Can be used to store some properties of a root entity which can be later the
 * base entity of a Kasper CQRS domain entity command.
 */
public class QueryResponse<RESULT extends QueryResult> extends KasperResponse {
    private static final long serialVersionUID = -6543664128786160837L;

    private final RESULT result;

    // ------------------------------------------------------------------------

    public static <R extends QueryResult> QueryResponse<R> error(final KasperReason reason) {
        return new QueryResponse<R>(checkNotNull(reason));
    }

    public static <R extends QueryResult> QueryResponse<R> refused(final KasperReason reason) {
        return new QueryResponse<R>(Status.REFUSED, checkNotNull(reason));
    }

    public static <R extends QueryResult> QueryResponse<R> error(final CoreReasonCode code) {
        return new QueryResponse<R>(new KasperReason(checkNotNull(code)));
    }

    public static <R extends QueryResult> QueryResponse<R> refused(final CoreReasonCode code) {
        return new QueryResponse<R>(Status.REFUSED, new KasperReason(checkNotNull(code)));
    }

    public static <R extends QueryResult> QueryResponse<R> of(final R result) {
        return new QueryResponse<R>(checkNotNull(result));
    }

    // ------------------------------------------------------------------------

    public QueryResponse(final KasperResponse response) {
        super(response);
        this.result = null;
    }

    public QueryResponse(final KasperResponse response, final RESULT result) {
        super(response);
        this.result = checkNotNull(result);
    }

    public QueryResponse(final QueryResponse<RESULT> response) {
        super(response);
        this.result = response.result;
    }

    public QueryResponse(final RESULT result) {
        super();
        this.result = checkNotNull(result);
    }
    
    public QueryResponse(final KasperReason reason) {
        this(Status.ERROR, checkNotNull(reason));
    }

    public QueryResponse(final Status status, final KasperReason reason) {
        super(status, reason);
        this.result = null;
    }

    // ------------------------------------------------------------------------

    public RESULT getResult() {
        return result;
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(final Object obj) {
        if (null == obj) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final QueryResponse other = (QueryResponse) obj;

        if ( ! super.equals(obj)) {
            return false;
        }

        return Objects.equal(this.result, other.result);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + com.google.common.base.Objects.hashCode(this.result);
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .addValue(super.toString())
                .addValue(this.result)
                .toString();
    }

}
