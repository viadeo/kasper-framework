package com.viadeo.kasper.cqrs.query.impl;

import com.codahale.metrics.Timer;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.RequestActorsChain;
import com.viadeo.kasper.cqrs.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.getMetricRegistry;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

public class QueryFiltersActor<Q extends Query, P extends QueryResult>
        implements QueryRequestActor<Q, P> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryFiltersActor.class);

    private final Collection<? extends QueryFilter<Q>> queryFilters;
    private final Collection<? extends QueryResponseFilter<P>> responseFilters;

    // ------------------------------------------------------------------------

    public QueryFiltersActor(final Collection<? extends QueryFilter<Q>> queryFilters,
                             final Collection<? extends QueryResponseFilter<P>> responseFilters) {
        this.queryFilters = checkNotNull(queryFilters);
        this.responseFilters = checkNotNull(responseFilters);
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
            final Timer.Context timerFilters = getMetricRegistry().timer(name(query.getClass(), "requests-query-filters-time")).time();

            for (final QueryFilter<Q> filter : queryFilters) {
                LOGGER.info(String.format("Apply query adapter %s", filter.getClass().getSimpleName()));
                newQuery = filter.adapt(context, query);
            }

            timerFilters.stop();
        }

        return newQuery;
    }

    // -----

    private QueryResponse<P> applyResponseFilters(final Class queryClass, final QueryResponse<P> response, final Context context) {
        QueryResponse<P> newResponse = response;

        if ((null != response.getResult()) && !responseFilters.isEmpty()) {
            final Timer.Context timerFilters = getMetricRegistry().timer(name(queryClass, "requests-response-filters-time")).time();
            for (final QueryResponseFilter<P> filter : responseFilters) {
                if (QueryResponseFilter.class.isAssignableFrom(filter.getClass())) {
                    LOGGER.info(String.format("Apply Response adapter %s", filter.getClass().getSimpleName()));

                    /* Apply filter */
                    newResponse = filter.adapt(context, response);
                }
            }
            timerFilters.stop();
        }

        return newResponse;
    }

}
