// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.viadeo.kasper.context.Context;
import org.axonframework.commandhandling.gateway.Timeout;
import org.axonframework.common.annotation.MetaData;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Axon interface definition for CommandGateway
 */
public interface CommandGateway {

    int DEFAULT_TIMEOUT_SEC = 60;

    /**
     * Fire an forget
     *
     * @param command the command
     * @param context the related context
     */
    void sendCommand(Command command, @MetaData(Context.METANAME) Context context);

    /**
     * Fire and get a Future
     *
     * @param command the command
     * @param context the related context
     * @return a future of a command response
     * @throws Exception an exception
     */
    Future<CommandResponse> sendCommandForFuture(Command command, @MetaData(Context.METANAME) Context context)
            throws Exception;

    /**
     * Wait for response
     *
     * @param command the command
     * @param context the related context
     * @return the command response
     * @throws Exception an exception
     */
    @Timeout(value = DEFAULT_TIMEOUT_SEC, unit = TimeUnit.SECONDS)
    CommandResponse sendCommandAndWaitForAResponse(Command command, @MetaData(Context.METANAME) Context context)
            throws Exception;

    /**
     * Wait for response and get exceptions
     *
     * @param command the command
     * @param context the related context
     * @return the command response
     * @throws Exception an exception
     */
    @Timeout(value = DEFAULT_TIMEOUT_SEC, unit = TimeUnit.SECONDS)
    CommandResponse sendCommandAndWaitForAResponseWithException(Command command, @MetaData(Context.METANAME) Context context)
            throws Exception;

    /**
     * Wait for command execution
     *
     * @param command the command
     * @param context the related context
     * @param timeout the delay of the timeout
     * @param unit the unit of the specified delay
     * @throws Exception an exception
     */
    void sendCommandAndWait(Command command, @MetaData(Context.METANAME) Context context, long timeout, TimeUnit unit)
            throws Exception;

    /**
     * Wait until command answers
     *
     * @param command the command
     * @param context the related context
     * @throws Exception an exception
     */
     void sendCommandAndWaitForever(Command command, @MetaData(Context.METANAME) Context context)
            throws Exception;

}
