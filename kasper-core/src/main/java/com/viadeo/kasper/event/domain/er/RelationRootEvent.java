// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain.er;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.er.RootRelation;
import com.viadeo.kasper.event.domain.RootEntityEvent;

/**
 *
 * Event on Kasper relation
 *
 */
public interface RelationRootEvent<D extends Domain, R extends RootRelation> extends RootEntityEvent<D, R> {

	/**
	 * @return the source entity id associated with the relation concerned by this event
	 */
	KasperID getSourceId();

	/**
	 * @return the target entity id associated with the relation concerned by this event
	 */
	KasperID getTargetId();

}
