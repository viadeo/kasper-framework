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
import com.viadeo.kasper.cqrs.RequestActorsChain;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.cqrs.query.QueryPayload;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.exception.KasperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

/** The Kasper gateway base implementation */
public class DefaultQueryGateway implements QueryGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultQueryGateway.class);
    private static final MetricRegistry METRICS = KasperMetrics.getRegistry();

    private static final Timer METRICLASSTIMER = METRICS.timer(name(QueryGateway.class, "requests-time"));
    private static final Histogram METRICLASSREQUESTSTIME = METRICS.histogram(name(QueryGateway.class, "requests-times"));
    private static final Meter METRICLASSREQUESTS = METRICS.meter(name(QueryGateway.class, "requests"));
    private static final Meter METRICLASSERRORS = METRICS.meter(name(QueryGateway.class, "errors"));

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
        final Timer.Context classTimer = METRICLASSTIMER.time();
        final Timer.Context timer = METRICS.timer(name(queryClass, "requests-time")).time();

        /* Sets current thread context */
        CurrentContext.set(context);

        // Search for associated service --------------------------------------
        LOGGER.debug("Retrieve request processor chain for query " + queryClass.getSimpleName());
        Optional<RequestActorsChain<Query, QueryResult<QueryPayload>>> optionalRequestChain =
                queryServicesLocator.getRequestActorChain(queryClass);

        if (!optionalRequestChain.isPresent()) {
            timer.close();
            classTimer.close();
            throw new KasperException("Unable to find the service implementing query class " + queryClass);
        }

        Exception exception = null;
        QueryResult<PAYLOAD> ret = null;

        try {
            LOGGER.info("Call actor chain for query " + queryClass.getSimpleName());
            ret = (QueryResult<PAYLOAD>) optionalRequestChain.get().next(query, context);
        } catch (final RuntimeException e) {
            exception = e;
        } catch (final Exception e) {
            exception = e;
        }

        /* Monitor the request calls */
        timer.stop();
        final long time = classTimer.stop();
        METRICLASSREQUESTSTIME.update(time);
        METRICS.histogram(name(queryClass, "requests-times")).update(time);
        METRICLASSREQUESTS.mark();
        METRICS.meter(name(queryClass, "requests")).mark();
        if ((null != exception) || ret.isError()) {
            METRICLASSERRORS.mark();
            METRICS.meter(name(queryClass, "errors")).mark();
        }

        if (null != exception) {
            throw exception;
        }

        return ret;
    }

    // -----------------------------------------------------------------------

    public void setQueryServicesLocator(final QueryServicesLocator queryServicesLocator) {
        this.queryServicesLocator = queryServicesLocator;
    }

}
