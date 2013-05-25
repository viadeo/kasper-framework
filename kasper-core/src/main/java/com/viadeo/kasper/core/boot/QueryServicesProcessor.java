// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.core.boot;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.cqrs.query.IQueryService;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.locators.IQueryServicesLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Process Kasper query services dynamic registration at kasper platform boot
 * 
 * @see XKasperQueryService
 */
public class QueryServicesProcessor extends AbstractSingletonAnnotationProcessor<XKasperQueryService, IQueryService<?,?>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(QueryServicesProcessor.class);

	/**
	 * The locator to register query services on
	 */
	private transient IQueryServicesLocator queryServicesLocator;

	// ------------------------------------------------------------------------

	/**
	 * Process Kasper query service
	 * 
	 * @see IQueryService
	 * @see com.viadeo.kasper.core.boot.IAnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	public void process(final Class<?> queryServiceClazz, final IQueryService<?,?> queryService) {
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
	public void setQueryServicesLocator(final IQueryServicesLocator queryServicesLocator) {
		this.queryServicesLocator = Preconditions.checkNotNull(queryServicesLocator);
	}

}

