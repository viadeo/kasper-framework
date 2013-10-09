// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event;

import com.google.common.base.Optional;

/**
 *
 * The Kasper error event
 *
 */
public interface ErrorEvent extends Event {

	/**
	 * @return the event's exception
	 */
	Optional<Exception> getException();

 	/**
	 * @return the event's exception
	 */
	Optional<String> getMessage();

  	/**
	 * @return the event's exception
	 */
	Optional<String> getCode();

}
