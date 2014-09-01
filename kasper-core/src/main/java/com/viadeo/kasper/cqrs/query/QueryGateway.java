// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.InterceptorFactory;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;

import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

/** The Kasper query gateway, used to result queries from the kasper platform */
public interface QueryGateway {

    static final String GLOBAL_TIMER_REQUESTS_TIME_NAME = name(QueryGateway.class, "requests-time");
    static final String GLOBAL_METER_REQUESTS_NAME = name(QueryGateway.class, "requests");
    static final String GLOBAL_METER_ERRORS_NAME = name(QueryGateway.class, "errors");

	/**
	 * @param context the query execution context
	 * @param query the query to be resulted
	 * @return the Data Transfer Object as an result
	 */
    <RESULT extends QueryResult> QueryResponse<RESULT> retrieve(Query query, Context context) throws Exception;

    /**
     * Register a query handler adapter to the gateway
     *
     * @param name the name of the adapter
     * @param adapter the query handler adapter to register
     * @param global the kind of the adapter. If true then the adapter will be applied to every query handler component.
     *               Otherwise the  adapter will be applied only on the component whose reference it
     */
    @Deprecated
    void register(final String name, final  QueryHandlerAdapter adapter, final boolean global);

    /**
     * Register a query handler to the gateway
     *
     * @param queryHandler the query handler to register
     */
    void register(final QueryHandler queryHandler);

    /**
     * Register an interceptor factory to the gateway
     *
     * @param interceptorFactory the query interceptor factory to register
     */
    void register(final InterceptorFactory interceptorFactory);
}
