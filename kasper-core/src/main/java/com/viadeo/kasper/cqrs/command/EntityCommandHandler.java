// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.IRepository;

/**
 *
 * The Kasper command handler for entity commands
 *
 * @param <AGR> Aggregate root
 * @param <C> Command
 * 
 * @see com.viadeo.kasper.ddd.AggregateRoot
 * @see Command
 * @see CommandHandler
 */
public interface EntityCommandHandler<C extends Command, AGR extends AggregateRoot>
		extends CommandHandler<C> {

	/**
	 * Generic parameter position for the handled command
	 */
	int COMMAND_PARAMETER_POSITION = 0;
	
	/**
	 * Generic parameter position for the handled entity
	 */
	int ENTITY_PARAMETER_POSITION = 1;
	
	/**
	 * @param repository the repository of the entity
	 */
	void setRepository(IRepository<AGR> repository);

	/**
	 * @return the repository of the repository
	 */
	<R extends IRepository<AGR>> R getRepository();

}
