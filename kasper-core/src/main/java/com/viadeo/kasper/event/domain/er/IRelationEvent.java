// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain.er;

import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.event.domain.IEntityEvent;

/**
 *
 * Event on Kasper relation
 *
 */
public interface IRelationEvent extends IEntityEvent {

	/**
	 * @return the source entity id associated with the relation concerned by this event
	 */
	IKasperID getSourceId();

	/**
	 * @return the target entity id associated with the relation concerned by this event
	 */
	IKasperID getTargetId();

}
