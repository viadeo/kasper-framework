// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.viadeo.kasper.er.IRelation;
import com.viadeo.kasper.er.annotation.XKasperRelation;

/**
 *
 * Process Kasper relation dynamic registration at platform boot
 *
 * @see XKasperRelation
 */
public class RelationsDocumentationProcessor extends AbstractDocumentationProcessor<XKasperRelation, IRelation<?,?>> {

	private final Logger LOGGER = LoggerFactory.getLogger(RelationsDocumentationProcessor.class);

	// ------------------------------------------------------------------------

	/**
	 * Process Kasper relation
	 * 
	 * @see IRelation
	 * @see com.viadeo.kasper.core.boot.IAnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void process(final Class<?> relationClazz) {
		this.LOGGER.info("Record on relation library : " + relationClazz.getName());

		//- Register the domain to the locator --------------------------------
		getKasperLibrary().recordRelation((Class<? extends IRelation<?, ?>>) relationClazz);
	}

}

