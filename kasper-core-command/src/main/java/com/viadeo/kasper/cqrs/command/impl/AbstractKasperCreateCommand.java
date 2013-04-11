// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.impl;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.cqrs.command.ICreateCommand;

/**
 *
 * Convenient base implementation for idToUse simple management
 * 
 */
public abstract class AbstractKasperCreateCommand implements ICreateCommand {
    private static final long serialVersionUID = -432287057793281452L;

	private IKasperID idToUse;

	// ------------------------------------------------------------------------

	public AbstractKasperCreateCommand() {

	}

	public AbstractKasperCreateCommand(final IKasperID providedId) {
		this.idToUse = Preconditions.checkNotNull(providedId);
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.command.ICreateCommand#setIdToUse(com.viadeo.kasper.IKasperID)
	 */
	@SuppressWarnings("unchecked") // To be ensured by caller
	@Override
	public <C extends ICreateCommand> C  setIdToUse(final IKasperID providedId) {
		this.idToUse = Preconditions.checkNotNull(providedId);
		return (C) this;
	}

	/**
	 * @see com.viadeo.kasper.cqrs.command.ICreateCommand#getIdToUse()
	 */
	@Override
	public Optional<IKasperID> getIdToUse() {
		return Optional.fromNullable(this.idToUse);
	}

}
