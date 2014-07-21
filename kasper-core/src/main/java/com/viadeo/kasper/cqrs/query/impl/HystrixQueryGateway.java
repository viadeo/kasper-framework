package com.viadeo.kasper.cqrs.query.impl;

import com.codahale.metrics.MetricRegistry;
import com.netflix.hystrix.HystrixCommand;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.resilience.HystrixCommandWithExceptionPolicy;
import com.viadeo.kasper.resilience.HystrixGateway;
import com.viadeo.kasper.resilience.HystrixHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decorator for kasper query gateway.
 * Used instead of KasperQueryGateway.<br>
 *
 * Add Hystrix features :
 * <ul>
 *     <li>circuit breaker</li>
 *     <li>thread pool isolation</li>
 *     <li>timeout on method</li>
 *     <li>fallbacks</li>
 * </ul>
 *
 * @see com.viadeo.kasper.cqrs.query.impl.KasperQueryGateway
 */
public class HystrixQueryGateway extends HystrixGateway implements QueryGateway{

    private static final Logger LOGGER = LoggerFactory.getLogger(HystrixQueryGateway.class);

    private final QueryGateway queryGateway;

    public HystrixQueryGateway(QueryGateway queryGateway, MetricRegistry metricRegistry) {
        super(metricRegistry);
        this.queryGateway = queryGateway;
    }

    @Override
    public <RESULT extends QueryResult> QueryResponse<RESULT> retrieve(final Query query, final Context context) throws Exception {

        HystrixCommand<QueryResponse<RESULT>> commandHystrixCommand = new HystrixCommandWithExceptionPolicy<QueryResponse<RESULT>>(HystrixHelper.buildSetter(query)) {

            @Override
            protected QueryResponse<RESULT> runWithException() throws Exception {
                return queryGateway.retrieve(query, context);
            }

            @Override
            protected QueryResponse<RESULT> getFallback() {
                reportFallback(query.getClass().getName());
                return QueryResponse.error(CoreReasonCode.INTERNAL_COMPONENT_TIMEOUT);
            }
        };

        return commandHystrixCommand.execute();
    }

    @Deprecated
    @Override
    public void register(String name, QueryHandlerAdapter adapter, boolean global) {
        queryGateway.register(name, adapter, global);
    }

    @Override
    public void register(QueryInterceptorFactory interceptorFactory) {
        queryGateway.register(interceptorFactory);
    }

    @Override
    public void register(QueryHandler queryHandler) {
        queryGateway.register(queryHandler);
    }

}
