// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er.impl;

import com.viadeo.kasper.ddd.impl.AbstractAggregateRoot;
import com.viadeo.kasper.er.IRootConcept;

/**
 * Base Kasper Concept Aggregate Root implementation
 *
 * @see com.viadeo.kasper.er.IConcept
 * @see IRootConcept
 * @see com.viadeo.kasper.ddd.IAggregateRoot
 */
public abstract class AbstractRootConcept 
		extends AbstractAggregateRoot
		implements IRootConcept {

	private static final long serialVersionUID = -1431592970440969164L;

}
