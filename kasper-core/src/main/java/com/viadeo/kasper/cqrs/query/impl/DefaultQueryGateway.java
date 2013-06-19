// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.IContext;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.locators.IQueryServicesLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/** The Kasper gateway base implementation */
public class DefaultQueryGateway implements IQueryGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultQueryGateway.class);

    private IQueryServicesLocator queryServicesLocator;

    // -----------------------------------------------------------------------

    @Override
    public <Q extends IQuery, DTO extends IQueryDTO> DTO retrieve(final Q query, final IContext context)
            throws Exception {
        checkNotNull(context);
        checkNotNull(query);

        DefaultQueryGateway.LOGGER.info("Call service for query " + query.getClass().getSimpleName());

        @SuppressWarnings("rawtypes")
        // Safe
        final Optional<IQueryService> service = queryServicesLocator.getServiceFromQueryClass(query.getClass());

        if (!service.isPresent()) {
            throw new KasperException("Unable to find the service implementing query class " + query.getClass());
        }

        DefaultQueryGateway.LOGGER.info("Call service " + service.get().getClass().getSimpleName());

        @SuppressWarnings({ "rawtypes", "unchecked" })
        // Safe
        final IQueryMessage message = new QueryMessage(context, query);

        // FIXME unsafe, make IQuery parameterized with the DTO type.
        return (DTO) service.get().retrieve(message);

    }

    // -----------------------------------------------------------------------

    public void setQueryServicesLocator(final IQueryServicesLocator queryServicesLocator) {
        this.queryServicesLocator = queryServicesLocator;
    }

}
