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
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryRuntimeException;
import com.viadeo.kasper.locators.IQueryServicesLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/** The Kasper gateway base implementation */
public class QueryGatewayBase implements IQueryGateway {

	private static final Logger LOGGER = LoggerFactory.getLogger(QueryGatewayBase.class);

	private IQueryServicesLocator queryServicesLocator;

	// -----------------------------------------------------------------------

	@Override
	public <Q extends IQuery, DTO extends IQueryDTO> DTO retrieve(final IContext context, final Q query)
			throws KasperQueryException {
		checkNotNull(context);
		checkNotNull(query);

		QueryGatewayBase.LOGGER.info("Call service for query " + query.getClass().getSimpleName());

		@SuppressWarnings("rawtypes") // Safe
		final Optional<IQueryService> service = queryServicesLocator.getServiceFromQueryClass(query.getClass());

		if (!service.isPresent()) {
			throw new KasperQueryRuntimeException(
                    "Unable to find the service implementing query class " + query.getClass());
		}

		QueryGatewayBase.LOGGER.info("Call service " + service.get().getClass().getSimpleName());

		@SuppressWarnings({"rawtypes", "unchecked"}) // Safe
		final IQueryMessage message = new QueryMessage(context, query);

        try {
            final DTO dto = (DTO) service.get().retrieve(message);
    		return dto;
        } catch (final Exception e) {
            if (e instanceof KasperQueryException) {
                throw (KasperQueryException) e;
            } else {
                throw new KasperQueryException(e.getMessage(), e);
            }
        }

	}

	// -----------------------------------------------------------------------

	public void setQueryServicesLocator(final IQueryServicesLocator queryServicesLocator) {
		this.queryServicesLocator = queryServicesLocator;
	}

}
