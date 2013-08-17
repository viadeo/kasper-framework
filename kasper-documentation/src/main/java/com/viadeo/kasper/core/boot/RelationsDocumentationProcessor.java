// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.viadeo.kasper.er.Relation;
import com.viadeo.kasper.er.annotation.XKasperRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Process Kasper relation dynamic registration at platform boot
 *
 * @see XKasperRelation
 */
public class RelationsDocumentationProcessor extends DocumentationProcessor<XKasperRelation, Relation<?,?>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(RelationsDocumentationProcessor.class);

	// ------------------------------------------------------------------------

    /**
	 * Process Kasper relation
	 * 
	 * @see com.viadeo.kasper.er.Relation
	 * @see AnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void process(final Class<?> relationClazz) {
		LOGGER.info("Record on relation library : " + relationClazz.getName());

		//- Register the domain to the locator --------------------------------
		getKasperLibrary().recordRelation((Class<? extends Relation<?, ?>>) relationClazz);
	}

}

