package com.viadeo.kasper.cqrs.query.impl;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.cqrs.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

public class QueryServiceActor<Q extends Query, PAYLOAD extends QueryPayload> implements RequestActor<Q, QueryResult<PAYLOAD>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryServiceActor.class);
    private static final MetricRegistry METRICS = KasperMetrics.getRegistry();

    private static final Timer METRICLASSTIMER = METRICS.timer(name(QueryServiceActor.class, "requests-time"));

    private final QueryService<Q, PAYLOAD> queryService;


    public QueryServiceActor(QueryService<Q, PAYLOAD> queryService) {
        this.queryService = queryService;
    }

    @Override
    public QueryResult<PAYLOAD> process(Q query, Context context, RequestActorChain<Q, QueryResult<PAYLOAD>> chain) throws Exception {
        /* Call the service */
        Exception exception = null;
        QueryResult<PAYLOAD> ret = null;

        final Timer.Context classTimer = METRICLASSTIMER.time();
        final Timer.Context timer = METRICS.timer(name(query.getClass(), "requests-time")).time();

        final com.viadeo.kasper.cqrs.query.QueryMessage message = new DefaultQueryMessage(context, query);

        try {
            try {
                LOGGER.info("Call service " + queryService.getClass().getSimpleName());

                ret = (QueryResult<PAYLOAD>) queryService.retrieve(message);

            } catch (final UnsupportedOperationException e) {
                if (AbstractQueryService.class.isAssignableFrom(queryService.getClass())) {
                    ret = (QueryResult<PAYLOAD>) ((AbstractQueryService) queryService).retrieve(message.getQuery());
                } else {
                    timer.close();
                    classTimer.close();
                    throw e;
                }
            }
        } catch (final RuntimeException e) {
            exception = e;
        } catch (Exception e) {
            exception = e;
        }

        /* Monitor the request calls */
        timer.stop();
        final long time = classTimer.stop();

        if (exception != null)
            throw exception;

        return ret;
    }
}
