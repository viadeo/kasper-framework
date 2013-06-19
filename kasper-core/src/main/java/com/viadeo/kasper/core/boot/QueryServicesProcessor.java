// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.core.boot;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.cqrs.query.QueryService;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Process Kasper query services dynamic registration at kasper platform boot
 * 
 * @see XKasperQueryService
 */
public class QueryServicesProcessor extends SingletonAnnotationProcessor<XKasperQueryService, QueryService<?,?>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(QueryServicesProcessor.class);

	/**
	 * The locator to register query services on
	 */
	private transient QueryServicesLocator queryServicesLocator;

	// ------------------------------------------------------------------------

	/**
	 * Process Kasper query service
	 * 
	 * @see com.viadeo.kasper.cqrs.query.QueryService
	 * @see AnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	public void process(final Class<?> queryServiceClazz, final QueryService<?,?> queryService) {
		LOGGER.info("Record on query services locator : " + queryServiceClazz.getName());

		final String serviceName;
		final XKasperQueryService annotation = queryServiceClazz.getAnnotation(XKasperQueryService.class);
		if (annotation.name().isEmpty()) {
			serviceName = queryServiceClazz.getSimpleName();
		} else {
			serviceName = annotation.name();
		}

		//- Register the query service to the locator -------------------------
		this.queryServicesLocator.registerService(serviceName, queryService);
	}

	// ------------------------------------------------------------------------

	/**
	 * @param queryServicesLocator the locator to register query services on
	 */
	public void setQueryServicesLocator(final QueryServicesLocator queryServicesLocator) {
		this.queryServicesLocator = Preconditions.checkNotNull(queryServicesLocator);
	}

}

