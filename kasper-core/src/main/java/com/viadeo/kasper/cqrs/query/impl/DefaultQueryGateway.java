// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.exception.KasperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/** The Kasper gateway base implementation */
public class DefaultQueryGateway implements QueryGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultQueryGateway.class);

    private QueryServicesLocator queryServicesLocator;

    // -----------------------------------------------------------------------

    @Override
    public <Q extends Query, DTO extends QueryDTO> DTO retrieve(final Q query, final Context context)
            throws Exception {

        checkNotNull(context);
        checkNotNull(query);

        // Search for associated service --------------------------------------
        LOGGER.debug("Retrieve service for query " + query.getClass().getSimpleName());

        @SuppressWarnings("rawtypes")
        // Safe
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
        DTO ret;
        try {
            LOGGER.info("Call service " + optService.get().getClass().getSimpleName());
            ret = (DTO) service.retrieve(message);
        } catch (final UnsupportedOperationException e) {
            if (AbstractQueryService.class.isAssignableFrom(service.getClass())) {
                ret = (DTO) ((AbstractQueryService) service).retrieve(message.getQuery());
            } else {
                throw e;
            }
        }

        /* Apply DTO filters if needed */
        if (null != ret) {
            for (final ServiceFilter filter : filters) {
                if (DTOFilter.class.isAssignableFrom(filter.getClass())) {
                    LOGGER.info(String.format("Apply DTO filter %s", filter.getClass().getSimpleName()));
                    ((DTOFilter) filter).filter(context, ret);
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
