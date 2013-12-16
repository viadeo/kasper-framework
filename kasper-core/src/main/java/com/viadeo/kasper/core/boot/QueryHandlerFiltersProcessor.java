// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.core.locators.QueryHandlersLocator;
import com.viadeo.kasper.cqrs.query.QueryHandlerFilter;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandlerFilter;
import com.viadeo.kasper.ddd.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Process Kasper query handlers dynamic registration at kasper platform boot
 * 
 * @see com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler
 */
public class QueryHandlerFiltersProcessor extends SingletonAnnotationProcessor<XKasperQueryHandlerFilter, QueryHandlerFilter> {
	private static final Logger LOGGER = LoggerFactory.getLogger(QueryHandlerFiltersProcessor.class);

	/**
	 * The locator to register query handlers on
	 */
	private transient QueryHandlersLocator queryHandlersLocator;

	// ------------------------------------------------------------------------

    @Override
    public boolean isAnnotationMandatory() {
        return false;
    }

	/**
	 * Process Kasper query handler
	 *
	 * @see com.viadeo.kasper.cqrs.query.QueryHandler
	 * @see com.viadeo.kasper.core.boot.AnnotationProcessor#process(Class)
	 */
	@Override
	public void process(final Class queryFilterClazz, final QueryHandlerFilter queryFilter) {
		LOGGER.info("Record adapter on query handlers locator : " + queryFilterClazz.getName());

		final String filterName;
		final XKasperQueryHandlerFilter annotation = (XKasperQueryHandlerFilter)
                queryFilterClazz.getAnnotation(XKasperQueryHandlerFilter.class);

		if (annotation.name().isEmpty()) {
			filterName = queryFilterClazz.getSimpleName();
		} else {
			filterName = annotation.name();
		}

        Class<? extends Domain> stickyDomainClass = null;
        if (!annotation.domain().equals(XKasperQueryHandlerFilter.NullDomain.class)) {
            stickyDomainClass = annotation.domain();
        }

        final boolean isGlobal = annotation.global();

        //- Register the query adapter to the locator -------------------------
		this.queryHandlersLocator.registerFilter(filterName, queryFilter, isGlobal, stickyDomainClass);
	}

	// ------------------------------------------------------------------------

	/**
	 * @param queryHandlersLocator the locator to register query handlers on
	 */
	public void setQueryHandlersLocator(final QueryHandlersLocator queryHandlersLocator) {
		this.queryHandlersLocator = Preconditions.checkNotNull(queryHandlersLocator);
	}

}
