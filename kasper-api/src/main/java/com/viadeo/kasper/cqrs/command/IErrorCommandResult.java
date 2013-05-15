// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.google.common.base.Optional;

/**
 *
 * Command result for errors
 */
// TODO: implement error codes with an IError(String errorCode, errorMessage) 
public interface IErrorCommandResult extends ICommandResult {

	/**
	 * @return the FUNCTIONAL error message (consider to allow exposition to end user)
	 */
	Optional<String> getErrorMessage();
	
	/**
	 * @return
	 */
	Optional<Exception> getErrorException();	
	
}
