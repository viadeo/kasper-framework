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
 * 
 * Process Kasper repositories dynamic registration at kasper platform boot
 *
 * @see com.viadeo.kasper.ddd.annotation.XKasperRepository
 */
public class ConceptsProcessor extends AbstractEntitiesProcessor<XKasperConcept, Concept> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConceptsProcessor.class);

	// ------------------------------------------------------------------------

	/**
	 * Process Kasper repository
	 *
	 * @see com.viadeo.kasper.ddd.IRepository
	 * @see com.viadeo.kasper.core.boot.AnnotationProcessor#process(Class)
	 */
	@Override
	public void process(final Class<?> conceptClass) {
        super.process(conceptClass);

        // Currently does nothing
	}

}

