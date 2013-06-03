// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain;

import com.viadeo.kasper.IKasperID;
import org.joda.time.DateTime;

/**
 *
 * A Kasper event related to an entity
 *
 */
public interface IEntityEvent extends IDomainEvent {

	/**
	 * @return the entity identifier associated with this entity event
	 */
	IKasperID getEntityId();

	/**
	 * @return the last modification date of the entity (creation/update/deletion)
	 */
	DateTime getEntityLastModificationDate();
	
}
