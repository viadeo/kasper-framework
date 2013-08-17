// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.cqrs.query.ServiceFilter;
import com.viadeo.kasper.cqrs.query.annotation.XKasperServiceFilter;
import com.viadeo.kasper.ddd.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Process Kasper query services dynamic registration at kasper platform boot
 * 
 * @see com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService
 */
public class ServiceFiltersProcessor extends SingletonAnnotationProcessor<XKasperServiceFilter, ServiceFilter> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceFiltersProcessor.class);

	/**
	 * The locator to register query services on
	 */
	private transient QueryServicesLocator queryServicesLocator;

	// ------------------------------------------------------------------------

	/**
	 * Process Kasper query service
	 *
	 * @see com.viadeo.kasper.cqrs.query.QueryService
	 * @see com.viadeo.kasper.core.boot.AnnotationProcessor#process(Class)
	 */
	@Override
	public void process(final Class<?> queryFilterClazz, final ServiceFilter queryFilter) {
		LOGGER.info("Record filter on query services locator : " + queryFilterClazz.getName());

		final String filterName;
		final XKasperServiceFilter annotation = queryFilterClazz.getAnnotation(XKasperServiceFilter.class);
		if (annotation.name().isEmpty()) {
			filterName = queryFilterClazz.getSimpleName();
		} else {
			filterName = annotation.name();
		}

        Class<? extends Domain> stickyDomainClass = null;
        if (!annotation.domain().equals(XKasperServiceFilter.NullDomain.class)) {
            stickyDomainClass = annotation.domain();
        }

        final boolean isGlobal = annotation.global();

        //- Register the query filter to the locator -------------------------
		this.queryServicesLocator.registerFilter(filterName, queryFilter, isGlobal, stickyDomainClass);
	}

	// ------------------------------------------------------------------------

	/**
	 * @param queryServicesLocator the locator to register query services on
	 */
	public void setQueryServicesLocator(final QueryServicesLocator queryServicesLocator) {
		this.queryServicesLocator = Preconditions.checkNotNull(queryServicesLocator);
	}

}
