// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.viadeo.kasper.cqrs.query.QueryService;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Process Kasper command dynamic registration at platform boot
 *
 * @see QueryService
 */
public class QueryServicesDocumentationProcessor extends DocumentationProcessor<XKasperQueryService, QueryService> {
	private static final Logger LOGGER = LoggerFactory.getLogger(QueryServicesDocumentationProcessor.class);

	// ------------------------------------------------------------------------

	/**
	 * Process Kasper query
	 * 
	 * @see com.viadeo.kasper.cqrs.query.QueryService
	 * @see AnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void process(final Class queryServiceClazz) {
		LOGGER.info("Record on query services library : " + queryServiceClazz.getName());
		
		//- Register the domain to the locator --------------------------------
		getKasperLibrary().recordQueryService((Class<? extends QueryService>) queryServiceClazz);
	}

}

