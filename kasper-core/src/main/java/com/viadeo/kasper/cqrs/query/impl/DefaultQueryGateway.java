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

        DefaultQueryGateway.LOGGER.info("Call service for query " + query.getClass().getSimpleName());

        @SuppressWarnings("rawtypes")
        // Safe
        final Optional<QueryService> optService = queryServicesLocator.getServiceFromQueryClass(query.getClass());

        if (!optService.isPresent()) {
            throw new KasperException("Unable to find the service implementing query class " + query.getClass());
        }

        DefaultQueryGateway.LOGGER.info("Call service " + optService.get().getClass().getSimpleName());

        @SuppressWarnings({ "rawtypes", "unchecked" }) // Safe
        final com.viadeo.kasper.cqrs.query.QueryMessage message = new QueryMessage(context, query);
        final QueryService service = optService.get();

        DTO ret;
        try {
            ret = (DTO) service.retrieve(message);
        } catch (final UnsupportedOperationException e) {
            if (AbstractQueryService.class.isAssignableFrom(service.getClass())) {
                ret = (DTO) ((AbstractQueryService) service).retrieve(message.getQuery());
            } else {
                throw e;
            }
        }

        return ret;
    }

    // -----------------------------------------------------------------------

    public void setQueryServicesLocator(final QueryServicesLocator queryServicesLocator) {
        this.queryServicesLocator = queryServicesLocator;
    }

}
