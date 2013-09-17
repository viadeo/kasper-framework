package com.viadeo.kasper.cqrs.query.impl;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.metrics.KasperMetrics;

import static com.google.common.base.Preconditions.*;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

import com.viadeo.kasper.cqrs.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class QueryFiltersActor<Q extends Query, P extends QueryPayload> implements RequestActor<Q, QueryResult<P>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryFiltersActor.class);
    private static final MetricRegistry METRICS = KasperMetrics.getRegistry();

    private final Collection<? extends QueryFilter<Q>> queryFilters;
    private final Collection<? extends ResultFilter<P>> resultFilters;

    public QueryFiltersActor(Collection<? extends QueryFilter<Q>> queryFilters, Collection<? extends ResultFilter<P>> resultFilters) {
        this.queryFilters = checkNotNull(queryFilters);
        this.resultFilters = checkNotNull(resultFilters);
    }

    @Override
    public QueryResult<P> process(Q query, Context context, RequestActorChain<Q, QueryResult<P>> chain) throws Exception {
        return applyResultFilters(query.getClass(), chain.next(applyQueryFilters(query, context), context), context);
    }

    Q applyQueryFilters(Q query, Context context) {
        if (!queryFilters.isEmpty()) {
            final Timer.Context timerFilters = METRICS.timer(name(query.getClass(), "requests-query-filters-time")).time();
            for (final QueryFilter<Q> filter : queryFilters) {
                LOGGER.info(String.format("Apply query filter %s", filter.getClass().getSimpleName()));

                query = filter.filter(context, query);
            }
            timerFilters.stop();
        }
        return query;
    }

    QueryResult<P> applyResultFilters(Class<?> queryClass, QueryResult<P> result, Context context) {
        if ((null != result.getPayload()) && !resultFilters.isEmpty()) {
            final Timer.Context timerFilters = METRICS.timer(name(queryClass, "requests-result-filters-time")).time();
            for (final ResultFilter<P> filter : resultFilters) {
                if (ResultFilter.class.isAssignableFrom(filter.getClass())) {
                    LOGGER.info(String.format("Apply Result filter %s", filter.getClass().getSimpleName()));

                        /* Apply filter */
                    result = filter.filter(context, result);
                }
            }
            timerFilters.stop();
        }

        return result;
    }
}
