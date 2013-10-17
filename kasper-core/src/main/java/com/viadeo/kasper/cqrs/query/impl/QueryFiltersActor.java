package com.viadeo.kasper.cqrs.query.impl;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.cqrs.RequestActorsChain;
import com.viadeo.kasper.cqrs.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

public class QueryFiltersActor<Q extends Query, P extends QueryResult>
        implements QueryRequestActor<Q, P> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryFiltersActor.class);
    private static final MetricRegistry METRICS = KasperMetrics.getRegistry();

    private final Collection<? extends QueryFilter<Q>> queryFilters;
    private final Collection<? extends ResponseFilter<P>> resultFilters;

    // ------------------------------------------------------------------------

    public QueryFiltersActor(final Collection<? extends QueryFilter<Q>> queryFilters,
                             final Collection<? extends ResponseFilter<P>> resultFilters) {
        this.queryFilters = checkNotNull(queryFilters);
        this.resultFilters = checkNotNull(resultFilters);
    }

    // ------------------------------------------------------------------------

    @Override
    public QueryResponse<P> process(final Q query, final Context context, final RequestActorsChain<Q, QueryResponse<P>> chain) throws Exception {
        return applyResponseFilters(query.getClass(), chain.next(applyQueryFilters(query, context), context), context);
    }

    // ------------------------------------------------------------------------

    private Q applyQueryFilters(final Q query, final Context context) {
        Q newQuery = query;

        if (!queryFilters.isEmpty()) {
            final Timer.Context timerFilters = METRICS.timer(name(query.getClass(), "requests-query-filters-time")).time();

            for (final QueryFilter<Q> filter : queryFilters) {
                LOGGER.info(String.format("Apply query filter %s", filter.getClass().getSimpleName()));
                newQuery = filter.filter(context, query);
            }

            timerFilters.stop();
        }

        return newQuery;
    }

    // -----

    private <R extends QueryResponse<P>> R applyResponseFilters(final Class queryClass, final R result, final Context context) {
        R newResponse = result;

        if ((null != result.getResult()) && !resultFilters.isEmpty()) {
            final Timer.Context timerFilters = METRICS.timer(name(queryClass, "requests-result-filters-time")).time();
            for (final ResponseFilter<P> filter : resultFilters) {
                if (ResponseFilter.class.isAssignableFrom(filter.getClass())) {
                    LOGGER.info(String.format("Apply Response filter %s", filter.getClass().getSimpleName()));

                    /* Apply filter */
                    newResponse = filter.filter(context, result);
                }
            }
            timerFilters.stop();
        }

        return newResponse;
    }

}
