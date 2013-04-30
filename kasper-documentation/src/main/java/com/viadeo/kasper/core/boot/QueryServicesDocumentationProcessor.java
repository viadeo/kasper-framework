// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;
import com.viadeo.kasper.cqrs.query.IQueryService;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;

/**
 * Process Kasper command dynamic registration at platform boot
 *
 * @see XKasperCommand
 */
public class QueryServicesDocumentationProcessor extends AbstractDocumentationProcessor<XKasperQueryService, IQueryService<?,?>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(QueryServicesDocumentationProcessor.class);

	// ------------------------------------------------------------------------

	/**
	 * Process Kasper query
	 * 
	 * @see IQueryService
	 * @see com.viadeo.kasper.core.boot.IAnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void process(final Class<?> queryServiceClazz) {
		LOGGER.info("Record on query services library : " + queryServiceClazz.getName());
		
		//- Register the domain to the locator --------------------------------
		getKasperLibrary().recordQueryService((Class<? extends IQueryService<?,?>>) queryServiceClazz);
	}

	
}

