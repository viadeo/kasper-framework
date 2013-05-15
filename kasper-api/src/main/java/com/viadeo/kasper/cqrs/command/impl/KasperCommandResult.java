// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.cqrs.command.ICommandResult;
import com.viadeo.kasper.cqrs.command.IErrorCommandResult;
import com.viadeo.kasper.cqrs.command.exceptions.KasperCommandRuntimeException;

/**
 *
 * Base Kasper command result implementation
 * 
 */
public class KasperCommandResult implements ICommandResult {
	private static final long serialVersionUID = 510867338463938247L;

	/**
	 * The current command status
	 */
	private final Status status;
	
	// ------------------------------------------------------------------------
	
	public KasperCommandResult(final Status status) {
		this.status = Preconditions.checkNotNull(status);
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * @see com.viadeo.kasper.cqrs.command.ICommandResult#getStatus()
	 */
	@Override
	public Status getStatus() {
		return status;
	}

	/**
	 * @see com.viadeo.kasper.cqrs.command.ICommandResult#isError()
	 */
	@Override
	public boolean isError() {
		return this.status.equals(Status.ERROR);
	}

	// ------------------------------------------------------------------------
	
	/**
	 * To be overriden, only checks status value
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object otherResult) {
		
		if (this == Preconditions.checkNotNull(otherResult)) {
			return true;
		}
		
		if (KasperCommandResult.class.isAssignableFrom(otherResult.getClass())) {
			final KasperCommandResult other = (KasperCommandResult) otherResult;
			return status.equals(other.status);
		}
		
		return false;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.status.hashCode();
	}

	// ------------------------------------------------------------------------	
	
	@Override
	public IErrorCommandResult asError() {
		if (this.isError()) {
			return (IErrorCommandResult) this;
		}
		throw new KasperCommandRuntimeException("Not an error result !");
	}
	
}
