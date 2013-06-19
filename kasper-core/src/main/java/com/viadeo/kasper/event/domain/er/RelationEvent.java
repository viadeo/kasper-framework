// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain.er;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.domain.EntityEvent;

/**
 *
 * Event on Kasper relation
 *
 */
public interface RelationEvent extends EntityEvent {

	/**
	 * @return the source entity id associated with the relation concerned by this event
	 */
	KasperID getSourceId();

	/**
	 * @return the target entity id associated with the relation concerned by this event
	 */
	KasperID getTargetId();

}
