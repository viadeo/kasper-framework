// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.impl;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.cqrs.command.IErrorCommandResult;
import com.viadeo.kasper.exception.KasperException;

/**
 *
 * A Kasper command result with error
 *
 */
public class KasperErrorCommandResult extends KasperCommandResult implements IErrorCommandResult  {
	private static final long serialVersionUID = -1598506967323747246L;

	/**
	 * (Optional) Error message (consider to expose it to end user)
	 */
	private final String errorMessage;
	
	/**
	 * (Optional) exception that generated the error on command execution
	 */
	private final Exception errorException;
	
	// ------------------------------------------------------------------------
	
	public KasperErrorCommandResult(final String message) {
		super(Status.ERROR);
		this.errorMessage = Preconditions.checkNotNull(message);
		this.errorException = new KasperException();
	}
	
	public KasperErrorCommandResult(final String message, final Exception exception) {
		super(Status.ERROR);
		this.errorMessage = Preconditions.checkNotNull(message);
		this.errorException = Preconditions.checkNotNull(exception);
	}	
	
	/**
	 * @deprecated for final api clients since it does not set an explicit user message 
	 *             but allowed for controlled internal usage
	 * @param exception
	 */
	public KasperErrorCommandResult(final Exception exception) {
		super(Status.ERROR);
		this.errorMessage = "";
		this.errorException = Preconditions.checkNotNull(exception);
	}		

	// ------------------------------------------------------------------------
	
	/**
	 * @see com.viadeo.kasper.cqrs.command.IErrorCommandResult#getErrorMessage()
	 */
	@Override
	public Optional<String> getErrorMessage() {
		return Optional.fromNullable(this.errorMessage);
	}

	// ------------------------------------------------------------------------
	
	/**
	 * @see com.viadeo.kasper.cqrs.command.IErrorCommandResult#getErrorException()
	 */
	@Override
	public Optional<Exception> getErrorException() {
		return Optional.fromNullable(this.errorException);
	}

	// ------------------------------------------------------------------------
	
	/**
	 * Warning: Limits equality on error message equality
	 * Should not be really used unless to check messages..
	 * 
	 * @see com.viadeo.kasper.cqrs.command.impl.KasperCommandResult#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object otherResult) {
		
		if (this == Preconditions.checkNotNull(otherResult)) {
			return true;
		}
		
		if (KasperErrorCommandResult.class.isAssignableFrom(otherResult.getClass())) {
			final KasperErrorCommandResult other = (KasperErrorCommandResult) otherResult;
			if (!other.errorMessage.isEmpty() && !this.errorMessage.isEmpty()) {
				return this.errorMessage.equals(other.errorMessage) && super.equals(otherResult);
			} if (other.errorMessage.equals(this.errorMessage)) {
				return super.equals(otherResult);
			}
		}
		
		return false;
	}	
	
	/**
	 * @see com.viadeo.kasper.cqrs.command.impl.KasperCommandResult#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(this.errorMessage, super.hashCode());
	}	
	
}
