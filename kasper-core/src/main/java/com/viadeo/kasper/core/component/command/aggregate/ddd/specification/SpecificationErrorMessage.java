// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.aggregate.ddd.specification;

import com.google.common.base.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A basic implementation 
 *
 */
public class SpecificationErrorMessage {

	private String message;
	
	// ------------------------------------------------------------------------
	
	/**
     * @return an optional message
	 * @see SpecificationErrorMessage#getMessage()
	 */
	public Optional<String> getMessage() {
		return Optional.fromNullable(this.message);
	}

	// ------------------------------------------------------------------------
	
	/**
     * @param message a message
	 * @see SpecificationErrorMessage#setMessage(java.lang.String)
	 */
	public void setMessage(final String message) {
		this.message = checkNotNull(message);
	}
	
}
