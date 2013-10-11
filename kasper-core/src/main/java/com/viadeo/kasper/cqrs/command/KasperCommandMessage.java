// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.viadeo.kasper.context.Context;

import java.io.Serializable;

/**
 *
 * Message used by command bus to transfer commands to handlers
 *
 * @param <C> the type of command used as answer of this message
 */
public interface KasperCommandMessage<C extends Command> extends Serializable {

	/**
	 * @return the command message execution context
	 * @see com.viadeo.kasper.context.Context
	 */
	Context getContext();

	/**
	 * @return the enclosed command
	 * @see Command
	 */
	C getCommand();

}
