// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.specification;

import com.google.common.base.Optional;

/**
 * A simple class to store a specification error
 *
 */
public interface ISpecificationErrorMessage {

	/**
	 * @param message the error message
	 */
	void setMessage(String message);
	
	/**
	 * @return the error message
	 */
	Optional<String> getMessage();
	
}
