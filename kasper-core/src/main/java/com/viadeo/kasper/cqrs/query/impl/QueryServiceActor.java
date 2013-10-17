// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.impl;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.cqrs.RequestActor;
import com.viadeo.kasper.cqrs.RequestActorsChain;
import com.viadeo.kasper.cqrs.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

public class QueryServiceActor<Q extends Query, ANSWER extends QueryResult> implements RequestActor<Q, QueryResponse<ANSWER>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryServiceActor.class);
    private static final MetricRegistry METRICS = KasperMetrics.getRegistry();

    private static final Timer METRICLASSTIMER = METRICS.timer(name(QueryServiceActor.class, "requests-time"));

    private final QueryService<Q, ANSWER> queryService;

    // ------------------------------------------------------------------------

    public QueryServiceActor(final QueryService<Q, ANSWER> queryService) {
        this.queryService = queryService;
    }

    // ------------------------------------------------------------------------

    @Override
    public QueryResponse<ANSWER> process(final Q query, final Context context,
                                        final RequestActorsChain<Q, QueryResponse<ANSWER>> chain) throws Exception {
        /* Call the service */
        Exception exception = null;
        QueryResponse<ANSWER> ret = null;

        final Timer.Context classTimer = METRICLASSTIMER.time();
        final Timer.Context timer = METRICS.timer(name(query.getClass(), "requests-time")).time();

        final QueryMessage message = new DefaultQueryMessage(context, query);

        try {
            try {

                LOGGER.info("Call service " + queryService.getClass().getSimpleName());
                ret = queryService.retrieve(message);

            } catch (final UnsupportedOperationException e) {
                if (AbstractQueryService.class.isAssignableFrom(queryService.getClass())) {
                    ret = (QueryResponse<ANSWER>) ((AbstractQueryService) queryService).retrieve(message.getQuery());
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

        if (exception != null)
            throw exception;

        return ret;
    }

}
