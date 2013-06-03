// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.google.common.base.Optional;
import com.viadeo.kasper.IKasperID;

/**
 *
 * Can be used for Kasper commands which will create entities
 * The submitted id can then be used by handlers as the created element's id
 *
 */
public interface ICreateCommand extends ICommand {

	/**
	 * @param providedId for the command handler to use this id for aggregate root creation
	 */
	<C extends ICreateCommand> C setIdToUse(IKasperID providedId);

	/**
	 * @return the (optional) user requested id to be used
	 */
	Optional<IKasperID> getIdToUse();

}
