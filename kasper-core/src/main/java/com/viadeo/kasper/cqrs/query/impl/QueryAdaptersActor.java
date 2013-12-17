package com.viadeo.kasper.cqrs.query.impl;

import com.codahale.metrics.Timer;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.Adapter;
import com.viadeo.kasper.cqrs.RequestActorsChain;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryRequestActor;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.getMetricRegistry;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

public class QueryAdaptersActor<Q extends Query, P extends QueryResult>
        implements QueryRequestActor<Q, P> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryAdaptersActor.class);

    private final Collection<Adapter<Q>> queryAdapters;
    private final Collection<Adapter<QueryResponse<P>>> responseAdapters;

    // ------------------------------------------------------------------------

    public QueryAdaptersActor(final Collection<Adapter<Q>> queryAdapters,
                              final Collection<Adapter<QueryResponse<P>>> responseAdapters) {
        this.queryAdapters = checkNotNull(queryAdapters);
        this.responseAdapters = checkNotNull(responseAdapters);
    }

    @Override
    public QueryResponse<P> process(final Q query, final Context context, final RequestActorsChain<Q, QueryResponse<P>> chain) throws Exception {
        Class<? extends Query> queryClass = query.getClass();

        QueryResponse<P> responses = chain.next(
                applyAdapters(getMetricRegistry().timer(name(queryClass, "requests-query-adapters-time")),
                        queryAdapters,
                        context,
                        query)
                , context);

        return applyAdapters(
                getMetricRegistry().timer(name(queryClass, "requests-response-adapters-time")),
                responseAdapters,
                context,
                responses
        );
    }

    // ------------------------------------------------------------------------

    private <ELEM> ELEM applyAdapters(final Timer timer,
                                      final Collection<Adapter<ELEM>> adapters,
                                      final Context context,
                                      final ELEM elem) {
        ELEM newElem = elem;

        if (!adapters.isEmpty()) {
            Timer.Context timerContext = timer.time();

            try{
                for (final Adapter<ELEM> adapter : adapters) {
                    LOGGER.info("Apply adapter {} on {}", adapter.getClass().getSimpleName(), elem.getClass().getSimpleName());
                    newElem = adapter.adapt(context, elem);
                }
            } finally {
                timerContext.stop();
            }
        }

        return newElem;
    }

}
