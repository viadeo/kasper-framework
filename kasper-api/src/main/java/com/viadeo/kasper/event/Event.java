// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.annotation.Immutable;
import com.viadeo.kasper.context.Context;

import java.io.Serializable;

/**
 *
 * The Kasper event
 *
 */
public interface Event extends Serializable, Immutable {

    /**
     * @return the UOW macro event id
     */
    Optional<KasperID> getUOWEventId();

    /**
     * Sets the associated uow event id
     *
     * @param uowEventId the uow event id
     */
    void setUOWEventId(KasperID uowEventId);

    /**
     * @return this event's id
     */
    KasperID getId();

	/**
	 * @return the event's context
	 */
	Optional<Context> getContext();

	/**
	 * @param context the event's context
	 */
	<E extends Event> E setContext(Context context);

}
