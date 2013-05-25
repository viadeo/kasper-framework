// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.viadeo.kasper.context.IContext;

import java.io.Serializable;

/**
 *
 * Message used by command bus to transfer commands to handlers
 *
 * @param <C> the type of command used as payload of this message
 */
public interface ICommandMessage<C extends ICommand> extends Serializable {

	/**
	 * @return the command message execution context
	 * @see IContext
	 */
	IContext getContext();

	/**
	 * @return the enclosed command
	 * @see ICommand
	 */
	C getCommand();

}
