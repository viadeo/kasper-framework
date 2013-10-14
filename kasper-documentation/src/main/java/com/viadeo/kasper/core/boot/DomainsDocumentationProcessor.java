// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Process Kasper domain dynamic registration at platform boot
 *
 * @see XKasperDomain
 */
public class DomainsDocumentationProcessor extends DocumentationProcessor<XKasperDomain, Domain> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DomainsDocumentationProcessor.class);

	// ------------------------------------------------------------------------

    /**
     * Annotation is optional for domains
     */
    public boolean isAnnotationMandatory() {
        return false;
    }

    /**
	 * Process Kasper domain
	 * 
	 * @see AnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void process(final Class domainClazz) {
		LOGGER.info("Record on domain library : " + domainClazz.getName());

		//- Register the domain to the locator --------------------------------
		getKasperLibrary().recordDomain((Class<? extends Domain>) domainClazz);
	}

}

