// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.axonframework.commandhandling.gateway.Timeout;
import org.axonframework.common.annotation.MetaData;

import com.viadeo.kasper.context.IContext;

/**
 *
 * Axon interface definition for CommandGateway
 * 
 */
public interface ICommandGateway {

	int DEFAULT_TIMEOUT_SEC = 20;
	
	/**
	 * Fire an forget
	 */
	void sendCommand(ICommand command, @MetaData(IContext.METANAME) IContext context);

	/**
	 * Fire and get a Future
	 */
	Future<ICommandResult> sendCommandForFuture(ICommand command, @MetaData(IContext.METANAME) IContext context);

	/**
	 * Wait for result
	 */
	@Timeout(value = DEFAULT_TIMEOUT_SEC, unit = TimeUnit.SECONDS)
	ICommandResult sendCommandAndWaitForAResult(ICommand command, @MetaData(IContext.METANAME) IContext context);

	/**
	 * Wait for result and get exceptions
	 */
	@Timeout(value = DEFAULT_TIMEOUT_SEC, unit = TimeUnit.SECONDS)
	ICommandResult sendCommandAndWaitForAResultWithException(ICommand command, @MetaData(IContext.METANAME) IContext context) throws TimeoutException, InterruptedException;

	/**
	 * Wait for command execution
	 */
	void sendCommandAndWait(ICommand command, @MetaData(IContext.METANAME) IContext context, long timeout, TimeUnit unit)
			throws TimeoutException, InterruptedException;

}
