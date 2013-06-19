// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.viadeo.kasper.context.IContext;
import org.axonframework.commandhandling.gateway.Timeout;
import org.axonframework.common.annotation.MetaData;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Axon interface definition for CommandGateway
 */
public interface ICommandGateway {

    int DEFAULT_TIMEOUT_SEC = 20;

    /**
     * Fire an forget
     */
    void sendCommand(ICommand command, @MetaData(IContext.METANAME) IContext context) throws Exception;

    /**
     * Fire and get a Future
     */
    Future<CommandResult> sendCommandForFuture(ICommand command, @MetaData(IContext.METANAME) IContext context)
            throws Exception;

    /**
     * Wait for result
     */
    @Timeout(value = DEFAULT_TIMEOUT_SEC, unit = TimeUnit.SECONDS)
    CommandResult sendCommandAndWaitForAResult(ICommand command, @MetaData(IContext.METANAME) IContext context)
            throws Exception;

    /**
     * Wait for result and get exceptions
     */
    @Timeout(value = DEFAULT_TIMEOUT_SEC, unit = TimeUnit.SECONDS)
    CommandResult sendCommandAndWaitForAResultWithException(ICommand command, @MetaData(IContext.METANAME) IContext context)
            throws Exception, TimeoutException, InterruptedException;

    /**
     * Wait for command execution
     */
    void sendCommandAndWait(ICommand command, @MetaData(IContext.METANAME) IContext context, long timeout, TimeUnit unit)
            throws Exception, TimeoutException, InterruptedException;

}
