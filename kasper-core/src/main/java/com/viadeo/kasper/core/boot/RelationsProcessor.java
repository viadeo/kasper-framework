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
 * Process Kasper repositories dynamic registration at kasper platform boot
 *
 * @see com.viadeo.kasper.ddd.annotation.XKasperRepository
 */
public class RelationsProcessor extends AbstractEntitiesProcessor<XKasperRelation, Relation> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RelationsProcessor.class);

	// ------------------------------------------------------------------------

	/**
	 * Process Kasper repository
	 *
	 * @see com.viadeo.kasper.ddd.IRepository
	 * @see AnnotationProcessor#process(Class)
	 */
	@Override
	public void process(final Class conceptClass) {
        super.process(conceptClass);

        // Currently does nothing
	}

}

