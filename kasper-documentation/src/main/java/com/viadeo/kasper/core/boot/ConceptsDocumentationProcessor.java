// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Process Kasper concept dynamic registration at platform boot
 *
 * @see XKasperConcept
 */
public class ConceptsDocumentationProcessor extends DocumentationProcessor<XKasperConcept, Concept> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConceptsDocumentationProcessor.class);

	// ------------------------------------------------------------------------

    /**
	 * Process Kasper concept
	 * 
	 * @see com.viadeo.kasper.er.Concept
	 * @see AnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void process(final Class conceptClazz) {
		LOGGER.info("Record on concept library : " + conceptClazz.getName());
		
		//- Register the domain to the locator --------------------------------
		getKasperLibrary().recordConcept((Class<? extends Concept>) conceptClazz);
	}

}

