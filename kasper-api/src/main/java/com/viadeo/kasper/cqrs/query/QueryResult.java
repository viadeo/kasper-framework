// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.annotation.Immutable;
import com.viadeo.kasper.cqrs.TransportMode;
import com.viadeo.kasper.cqrs.command.http.HTTPCommandResult;
import com.viadeo.kasper.cqrs.query.http.HTTPQueryResult;
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
public class QueryResult<ANSWER extends QueryAnswer> implements Serializable, Immutable {
    private static final long serialVersionUID = -6543664128786160837L;

    private final ANSWER answer;
    private final KasperError error;

    // ------------------------------------------------------------------------

    public static <P extends QueryAnswer> QueryResult<P> of(final KasperError error) {
        return new QueryResult<P>(error);
    }

    public static <P extends QueryAnswer> QueryResult<P> of(final P result) {
        return new QueryResult<P>(result);
    }

    // ------------------------------------------------------------------------

    public QueryResult(final QueryResult<ANSWER> result) {
        this.answer = result.answer;
        this.error = result.error;
    }

    public QueryResult(final ANSWER answer) {
        this.answer = checkNotNull(answer);
        this.error = null;
    }
    
    public QueryResult(final KasperError error) {
        this.answer = null;
        this.error = checkNotNull(error);
    }

    // ------------------------------------------------------------------------

    public KasperError getError() {
        return error;
    }
    
    public ANSWER getAnswer() {
        return answer;
    }
    
    public boolean isError() {
        return error != null;
    }

    // ------------------------------------------------------------------------

    public TransportMode getTransportMode() {
         if (HTTPQueryResult.class.isAssignableFrom(this.getClass())) {
             return TransportMode.HTTP;
        }
        throw new UnsupportedOperationException();
    }

    public HTTPQueryResult asHttp() {
        if (HTTPQueryResult.class.isAssignableFrom(this.getClass())) {
            return (HTTPQueryResult) this;
        }
        throw new KasperException("Not an HTTP query result");
    }

}
