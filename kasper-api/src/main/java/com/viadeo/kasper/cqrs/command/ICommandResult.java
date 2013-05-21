// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import java.io.Serializable;

/**
 *
 * Base command handler result : just communicate the handling status
 * On asynchronous command handling, the result will not be directly communicated to clients
 * 
 */
public interface ICommandResult extends Serializable {

	/**
	 * Accepted values for command result statuses
	 */
	public static enum Status {
		OK,			/** All is ok */ 
		REFUSED,    /** Refused by some intermediate validation mechanisms */
		ERROR       /** Just error in command handling or domain business */
	}
	
	/**
	 * @return the current command result execution status
	 */
	Status getStatus();
	
	/**
	 * @return true if this command has resulted to an error
	 */
	boolean isError();
	
	/**
	 * @return this result as an error result
	 * @throws runtime exception is not an error result
	 */
	IErrorCommandResult asError();
	
}
