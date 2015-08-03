// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.core.component.MeasuredHandler;
import com.viadeo.kasper.core.component.query.gateway.QueryGateway;

public class MeasuredQueryHandler
        extends MeasuredHandler<Query, QueryMessage<Query>, QueryResponse<QueryResult>, QueryHandler<Query, QueryResult>>
        implements QueryHandler<Query, QueryResult>
{

    public MeasuredQueryHandler(
            final MetricRegistry metricRegistry,
            final QueryHandler handler
    ) {
        super(metricRegistry, handler, QueryGateway.class);
    }

    @Override
    public QueryResponse<QueryResult> error(KasperReason reason) {
        return QueryResponse.error(reason);
    }

    @Override
    public Class<QueryResult> getResultClass() {
        return handler.getResultClass();
    }
}
