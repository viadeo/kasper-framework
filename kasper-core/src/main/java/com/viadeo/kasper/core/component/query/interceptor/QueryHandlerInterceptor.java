// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query.interceptor;

import com.codahale.metrics.Timer;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.QueryMessage;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.getMetricRegistry;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

public class QueryHandlerInterceptor<QUERY extends Query, RESULT extends QueryResult>
        implements QueryInterceptor<QUERY, RESULT> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryHandlerInterceptor.class);

    public static final String GLOBAL_TIMER_INTERCEPTOR_REQUESTS_TIME_NAME = name(QueryHandlerInterceptor.class, "interceptor-requests-time");

    private final QueryHandler<QUERY, RESULT> queryHandler;

    // ------------------------------------------------------------------------

    public QueryHandlerInterceptor(final QueryHandler<QUERY, RESULT> queryHandler) {
        this.queryHandler = checkNotNull(queryHandler);
    }

    // ------------------------------------------------------------------------

    @Override
    public QueryResponse<RESULT> process(final QUERY query,
                                         final Context context,
                                         final InterceptorChain<QUERY, QueryResponse<RESULT>> chain)
            throws Exception {

        /* Call the handler */
        Exception exception = null;
        QueryResponse<RESULT> ret = null;

        final Timer.Context classTimer = getMetricRegistry().timer(GLOBAL_TIMER_INTERCEPTOR_REQUESTS_TIME_NAME).time();
        final Timer.Context timer = getMetricRegistry().timer(name(query.getClass(), "interceptor-requests-time")).time();

        final QueryMessage<QUERY> message = new QueryMessage<>(context, query);

        try {

            try {

                LOGGER.info("Call handler " + queryHandler.getClass().getSimpleName());
                ret = queryHandler.retrieve(message);

            } catch (final UnsupportedOperationException e) {
                if (QueryHandler.class.isAssignableFrom(queryHandler.getClass())) {
                    ret = queryHandler.retrieve(message.getQuery());
                } else {
                    timer.close();
                    classTimer.close();
                    throw e;
                }
            }

        } catch (final RuntimeException e) {
            exception = e;
        } catch (final Exception e) {
            exception = e;
        }

        /* Monitor the request calls */
        timer.stop();
        classTimer.stop();

        if (null != exception) {
            throw exception;
        }

        return ret;
    }

}
