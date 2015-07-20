// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.bundle.descriptor;

import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.api.component.query.QueryResult;

import static com.google.common.base.Preconditions.checkNotNull;

public class QueryHandlerDescriptor implements KasperComponentDescriptor {

    private final Class<? extends QueryHandler> queryHandlerClass;
    private final Class<? extends QueryResult> queryResultClass;
    private final Class<? extends Query> queryClass;

    // ------------------------------------------------------------------------

    public QueryHandlerDescriptor(final Class<? extends QueryHandler> queryHandlerClass,
                                  final Class<? extends Query> queryClass,
                                  final Class<? extends QueryResult> queryResultClass) {
        this.queryHandlerClass = checkNotNull(queryHandlerClass);
        this.queryClass = checkNotNull(queryClass);
        this.queryResultClass = checkNotNull(queryResultClass);
    }

    // ------------------------------------------------------------------------

    @Override
    public Class<? extends QueryHandler> getReferenceClass() {
        return queryHandlerClass;
    }

    public Class<? extends QueryResult> getQueryResultClass() {
        return queryResultClass;
    }

    public Class<? extends Query> getQueryClass() {
        return queryClass;
    }

}
