// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

/**
 *
 * The Kasper command handler
 *
 * @param <C> Command
 * 
 * @see Command
 */
public interface CommandHandler<C extends Command> extends org.axonframework.commandhandling.CommandHandler<C> {

	/**
	 * Generic parameter position for the handled command
	 */
	int COMMAND_PARAMETER_POSITION = 0;

}
