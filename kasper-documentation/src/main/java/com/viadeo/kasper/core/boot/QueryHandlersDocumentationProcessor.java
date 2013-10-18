// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Process Kasper command dynamic registration at platform boot
 *
 * @see QueryHandler
 */
public class QueryHandlersDocumentationProcessor extends DocumentationProcessor<XKasperQueryHandler, QueryHandler> {
	private static final Logger LOGGER = LoggerFactory.getLogger(QueryHandlersDocumentationProcessor.class);

	// ------------------------------------------------------------------------

	/**
	 * Process Kasper query
	 * 
	 * @see com.viadeo.kasper.cqrs.query.QueryHandler
	 * @see AnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void process(final Class queryHandlerClazz) {
		LOGGER.info("Record on query handlers library : " + queryHandlerClazz.getName());
		
		//- Register the domain to the locator --------------------------------
		getKasperLibrary().recordQueryHandler((Class<? extends QueryHandler>) queryHandlerClazz);
	}

}

