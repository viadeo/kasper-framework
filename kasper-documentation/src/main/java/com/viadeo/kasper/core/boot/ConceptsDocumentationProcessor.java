// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.viadeo.kasper.er.IConcept;
import com.viadeo.kasper.er.annotation.XKasperConcept;

/**
 * Process Kasper concept dynamic registration at platform boot
 *
 * @see XKasperConcept
 */
public class ConceptsDocumentationProcessor extends AbstractDocumentationProcessor<XKasperConcept, IConcept> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConceptsDocumentationProcessor.class);

	// ------------------------------------------------------------------------

	/**
	 * Process Kasper concept
	 * 
	 * @see IConcept
	 * @see com.viadeo.kasper.core.boot.IAnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void process(final Class<?> conceptClazz) {
		LOGGER.info("Record on concept library : " + conceptClazz.getName());
		
		//- Register the domain to the locator --------------------------------
		getKasperLibrary().recordConcept((Class<? extends IConcept>) conceptClazz);
	}

}

