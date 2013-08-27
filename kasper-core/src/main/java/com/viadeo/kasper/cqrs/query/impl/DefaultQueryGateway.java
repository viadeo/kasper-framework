// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.impl;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Optional;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.context.CurrentContext;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.exception.KasperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static com.codahale.metrics.MetricRegistry.name;
import static com.google.common.base.Preconditions.checkNotNull;

/** The Kasper gateway base implementation */
public class DefaultQueryGateway implements QueryGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultQueryGateway.class);
    private static final MetricRegistry metrics = KasperMetrics.getRegistry();

    private static final Timer metricClassTimer = metrics.timer(name(CommandGateway.class, "requests-time"));
    private static final Histogram metricClassRequestsTimes = metrics.histogram(name(CommandGateway.class, "requests-times"));
    private static final Meter metricClassRequests = metrics.meter(name(CommandGateway.class, "requests"));
    private static final Meter metricClassErrors = metrics.meter(name(CommandGateway.class, "errors"));

    private QueryServicesLocator queryServicesLocator;

    // -----------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public <PAYLOAD extends QueryPayload> QueryResult<PAYLOAD> retrieve(final Query query, final Context context)
            throws Exception {

        checkNotNull(context);
        checkNotNull(query);

        final Class<? extends Query> queryClass = query.getClass();

        /* Start request timer */
        final Timer.Context classTimer = metricClassTimer.time();
        final Timer.Context timer = metrics.timer(name(queryClass, "requests-time")).time();

        /* Sets current thread context */
        CurrentContext.set(context);

        // Search for associated service --------------------------------------
        LOGGER.debug("Retrieve service for query " + queryClass.getSimpleName());

        @SuppressWarnings("rawtypes") // Safe
        final Optional<QueryService> optService = queryServicesLocator.getServiceFromQueryClass(queryClass);

        if (!optService.isPresent()) {
            timer.close();
            classTimer.close();;
            throw new KasperException("Unable to find the service implementing query class " + queryClass);
        }

        // Apply filters and call service -------------------------------------
        @SuppressWarnings({ "rawtypes"}) // Safe
        final com.viadeo.kasper.cqrs.query.QueryMessage message = new DefaultQueryMessage(context, query);
        final QueryService service = optService.get();

        /* Apply query filters if needed */
        final Class<? extends QueryService<?, ?>> serviceClass = (Class<? extends QueryService<?, ?>>) service.getClass();
        final Collection<ServiceFilter> filters = this.queryServicesLocator.getFiltersForServiceClass(serviceClass);
        if (!filters.isEmpty()) {
            final Timer.Context timerFilters = metrics.timer(name(queryClass, "requests-query-filters-time")).time();
            for (final ServiceFilter filter : filters) {
                if (QueryFilter.class.isAssignableFrom(filter.getClass())) {
                    LOGGER.info(String.format("Apply query filter %s", filter.getClass().getSimpleName()));
                    ((QueryFilter) filter).filter(context, query);
                }
            }
            timerFilters.stop();
        }

        /* Call the service */
        QueryResult<PAYLOAD> ret;
        try { LOGGER.info("Call service " + optService.get().getClass().getSimpleName());

            ret = (QueryResult<PAYLOAD>) service.retrieve(message);

        } catch (final UnsupportedOperationException e) {
            if (AbstractQueryService.class.isAssignableFrom(service.getClass())) {
                ret = (QueryResult<PAYLOAD>) ((AbstractQueryService) service).retrieve(message.getQuery());
            } else {
                timer.close();
                classTimer.close();
                throw e;
            }
        }
        
        checkNotNull(ret);

        /* Apply Result filters if needed */
        if ((null != ret.getPayload()) && !filters.isEmpty()) {
            final Timer.Context timerFilters = metrics.timer(name(queryClass, "requests-result-filters-time")).time();
            for (final ServiceFilter filter : filters) {
                if (ResultFilter.class.isAssignableFrom(filter.getClass())) {
                    LOGGER.info(String.format("Apply Result filter %s", filter.getClass().getSimpleName()));
                    ((ResultFilter) filter).filter(context, ret);
                }
            }
            timerFilters.stop();
        }

        /* Monitor the request calls */
        timer.stop();
        final long time = classTimer.stop();
        metricClassRequestsTimes.update(time);
        metrics.histogram(name(queryClass, "requests-times")).update(time);
        metricClassRequests.mark();
        metrics.meter(name(queryClass, "requests")).mark();
        if (ret.isError()) {
            metricClassErrors.mark();
            metrics.meter(name(queryClass, "errors")).mark();
        }

        return ret;
    }

    // -----------------------------------------------------------------------

    public void setQueryServicesLocator(final QueryServicesLocator queryServicesLocator) {
        this.queryServicesLocator = queryServicesLocator;
    }

}
