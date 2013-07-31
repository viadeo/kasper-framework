// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.Context;

import java.io.Serializable;

/**
 *
 * The Kasper event
 *
 */
public interface Event extends Serializable {

	/**
	 * @return the event's context
	 */
	Optional<Context> getContext();

	/**
	 * @param context the event's context
	 */
	void setContext(Context context);

}
