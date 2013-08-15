// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.AbstractContext;
import com.viadeo.kasper.context.impl.DefaultKasperId;
import com.viadeo.kasper.core.context.CurrentContext;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.exception.KasperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/** The Kasper gateway base implementation */
public class DefaultQueryGateway implements QueryGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultQueryGateway.class);

    private QueryServicesLocator queryServicesLocator;

    // -----------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public <RES extends QueryResult> RES retrieve(final Query query, final Context context)
            throws Exception {

        checkNotNull(context);
        checkNotNull(query);

        CurrentContext.set(context);

        // Search for associated service --------------------------------------
        LOGGER.debug("Retrieve service for query " + query.getClass().getSimpleName());

        @SuppressWarnings("rawtypes") // Safe
        final Optional<QueryService> optService = queryServicesLocator.getServiceFromQueryClass(query.getClass());

        if (!optService.isPresent()) {
            throw new KasperException("Unable to find the service implementing query class " + query.getClass());
        }

        // Apply filters and call service -------------------------------------
        @SuppressWarnings({ "rawtypes", "unchecked" }) // Safe
        final com.viadeo.kasper.cqrs.query.QueryMessage message = new DefaultQueryMessage(context, query);
        final QueryService service = optService.get();

        /* Apply query filters if needed */
        final Class<? extends QueryService<?, ?>> serviceClass = (Class<? extends QueryService<?, ?>>) service.getClass();
        final Collection<ServiceFilter> filters = this.queryServicesLocator.getFiltersForServiceClass(serviceClass);
        for (final ServiceFilter filter : filters) {
            if (QueryFilter.class.isAssignableFrom(filter.getClass())) {
                LOGGER.info(String.format("Apply query filter %s", filter.getClass().getSimpleName()));
                ((QueryFilter) filter).filter(context, query);
            }
        }

        /* Call the service */
        RES ret;
        try { LOGGER.info("Call service " + optService.get().getClass().getSimpleName());

            ret = (RES) service.retrieve(message);

        } catch (final UnsupportedOperationException e) {
            if (AbstractQueryService.class.isAssignableFrom(service.getClass())) {
                ret = (RES) ((AbstractQueryService) service).retrieve(message.getQuery());
            } else {
                throw e;
            }
        }

        /* Apply Result filters if needed */
        if (null != ret) {
            for (final ServiceFilter filter : filters) {
                if (ResultFilter.class.isAssignableFrom(filter.getClass())) {
                    LOGGER.info(String.format("Apply Result filter %s", filter.getClass().getSimpleName()));
                    ((ResultFilter) filter).filter(context, ret);
                }
            }
        }

        return ret;
    }

    // -----------------------------------------------------------------------

    public void setQueryServicesLocator(final QueryServicesLocator queryServicesLocator) {
        this.queryServicesLocator = queryServicesLocator;
    }

}
