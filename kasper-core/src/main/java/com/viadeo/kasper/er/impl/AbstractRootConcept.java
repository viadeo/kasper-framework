// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er.impl;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.impl.AbstractAggregateRoot;
import com.viadeo.kasper.er.RootConcept;

/**
 * Base Kasper Concept Aggregate Root implementation
 *
 * @see com.viadeo.kasper.er.Concept
 * @see com.viadeo.kasper.er.RootConcept
 * @see com.viadeo.kasper.ddd.AggregateRoot
 */
public abstract class AbstractRootConcept 
		extends AbstractAggregateRoot<KasperID>
		implements RootConcept {

	private static final long serialVersionUID = -1431592970440969164L;

}
