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
	private final String message;
	
	/**
	 * (Optional) exception that generated the error on command execution
	 */
	private final Exception exception;
	
	// ------------------------------------------------------------------------
	
	public KasperErrorCommandResult(final String message) {
		super(Status.ERROR);
		this.message = Preconditions.checkNotNull(message);
		this.exception = null;
	}
	
	public KasperErrorCommandResult(final String message, final Exception exception) {
		super(Status.ERROR);
		this.message = Preconditions.checkNotNull(message);
		this.exception = Preconditions.checkNotNull(exception);
	}	
	
	/**
	 * @deprecated for final api clients since it does not set an explicit user message 
	 *             but allowed for controlled internal usage
	 * @param exception
	 */
	public KasperErrorCommandResult(final Exception exception) {
		super(Status.ERROR);
		this.message = null;
		this.exception = Preconditions.checkNotNull(exception);
	}		

	// ------------------------------------------------------------------------
	
	/**
	 * @see com.viadeo.kasper.cqrs.command.IErrorCommandResult#getErrorMessage()
	 */
	@Override
	public Optional<String> getErrorMessage() {
		return Optional.fromNullable(this.message);
	}

	// ------------------------------------------------------------------------
	
	/**
	 * @see com.viadeo.kasper.cqrs.command.IErrorCommandResult#getErrorException()
	 */
	@Override
	public Optional<Exception> getErrorException() {
		return Optional.fromNullable(this.exception);
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
			if ((null != other.message) && (null != this.message)) {
				return this.message.equals(other.message) && super.equals(otherResult);
			} if (other.message == this.message) {
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
		return Objects.hashCode(this.message, super.hashCode());
	}	
	
}
