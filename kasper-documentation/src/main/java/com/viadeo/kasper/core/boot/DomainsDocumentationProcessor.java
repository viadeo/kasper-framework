// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.viadeo.kasper.IDomain;
import com.viadeo.kasper.ddd.IInternalDomain;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;

/**
 *
 * Process Kasper domain dynamic registration at platform boot
 *
 * @see XKasperDomain
 */
public class DomainsDocumentationProcessor extends AbstractDocumentationProcessor<XKasperDomain, IInternalDomain> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DomainsDocumentationProcessor.class);

	// ------------------------------------------------------------------------

	/**
	 * Process Kasper domain
	 * 
	 * @see IDomain
	 * @see com.viadeo.kasper.core.boot.IAnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void process(final Class<?> domainClazz) {
		LOGGER.info("Record on domain library : " + domainClazz.getName());

		//- Register the domain to the locator --------------------------------
		getKasperLibrary().recordDomain((Class<? extends IInternalDomain>) domainClazz);
	}

}

