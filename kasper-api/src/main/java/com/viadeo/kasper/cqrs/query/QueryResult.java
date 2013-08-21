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

/**
 * Data Transfer Object
 *
 * Represents an anemic transfer entity, in the Query semantics
 *
 * Can be used to store some properties of a root entity which can be later the
 * base entity of a Kasper CQRS domain entity command.
 */
// FIXME should result be serializable and immutable?
public class QueryResult<Result> implements Serializable, Immutable {

    private static final long serialVersionUID = -6543664128786160837L;
    private final Result result;
    // downside => allows only one kind of error
    private final KasperError error;
    
    public QueryResult(Result result) {
        this.result = result;
        this.error = null;
    }
    
    public QueryResult(KasperError error) {
        this.result = null;
        this.error = error;
    }
    
    // allows to reduce code removes the need of <XXX> in new QueryResult<XXX>(result);
    public static <R> QueryResult<R> of(KasperError error) {
        return new QueryResult<R>(error);
    }
    
    public static <R> QueryResult<R> of(R result) {
        return new QueryResult<R>(result);
    }
    
    public KasperError getError() {
        return error;
    }
    
    public Result getResult() {
        return result;
    }
    
    public boolean isError() {
        return error != null;
    }
}
