// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.annotation.XKasperRepository;

/**
 *
 * Process Kasper repository dynamic registration at kasper boot
 *
 * @see XKasperRepository
 */
public class RepositoriesDocumentationProcessor extends AbstractDocumentationProcessor<XKasperRepository, IRepository<?>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RepositoriesDocumentationProcessor.class);

	// ------------------------------------------------------------------------

	/**
	 * Process Kasper repository
	 * 
	 * @see IRepository
	 * @see com.viadeo.kasper.core.boot.IAnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void process(final Class<?> repositoryClazz) {
		LOGGER.info("Record on repository library : " + repositoryClazz.getName());
		
		//- Register the domain to the locator --------------------------------
		getKasperLibrary().recordRepository((Class<? extends IRepository<?>>) repositoryClazz);
	}


}

