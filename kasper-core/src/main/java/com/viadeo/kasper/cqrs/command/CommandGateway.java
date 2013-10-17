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

    int DEFAULT_TIMEOUT_SEC = 20;

    /**
     * Fire an forget
     */
    void sendCommand(Command command, @MetaData(Context.METANAME) Context context) throws Exception;

    /**
     * Fire and get a Future
     */
    Future<CommandResponse> sendCommandForFuture(Command command, @MetaData(Context.METANAME) Context context)
            throws Exception;

    /**
     * Wait for result
     */
    @Timeout(value = DEFAULT_TIMEOUT_SEC, unit = TimeUnit.SECONDS)
    CommandResponse sendCommandAndWaitForAResponse(Command command, @MetaData(Context.METANAME) Context context)
            throws Exception;

    /**
     * Wait for result and get exceptions
     */
    @Timeout(value = DEFAULT_TIMEOUT_SEC, unit = TimeUnit.SECONDS)
    CommandResponse sendCommandAndWaitForAResponseWithException(Command command, @MetaData(Context.METANAME) Context context)
            throws Exception;

    /**
     * Wait for command execution
     */
    void sendCommandAndWait(Command command, @MetaData(Context.METANAME) Context context, long timeout, TimeUnit unit)
            throws Exception;

}
