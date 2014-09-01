// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.interceptor.InterceptorFactory;
import org.axonframework.commandhandling.gateway.Timeout;
import org.axonframework.common.annotation.MetaData;

import javax.validation.constraints.NotNull;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Axon interface definition for CommandGateway<br>
 *
 * <p>
 *     Return type
 *     <hr>
 * <pre>
 *     void(if not timeout is set)   ----\
 *                                        ===> Return immediately
 *     Future                        --- /
 *
 *     OtherType  ===> Wait for result (can throw ClassCastException)
 * </pre>
 *
 * The declared return value of a method will also affect its behavior:
 * <ul>
 *      <li>
 * A void return type will cause the method to return immediately, unless there are other indications on the method that one would want to wait, such as a timeout or declared exceptions.
 *      </li>
 *      <li>
 * A Future return type will cause the method to return immediately. You can access the result of the Command Handler using the Future instance returned from the method. Exceptions and timeouts declared on the method are ignored.
 *      </li>
 *      <li>
 * Any other return type will cause the method to block until a result is available. The result is cast to the return type (causing a ClassCastException if the types don't match).
 *      </li>
 * </ul>
 * </p>
 *
 * <p>
 *     Exceptions:
 *     <hr>
 *     <ul>
 *         <li>
 * Any declared checked exception will be thrown if the Command Handler (or an interceptor) threw an exceptions of that type. If a checked exception is thrown that has not been declared, it is wrapped in a CommandExecutionException, which is a RuntimeException.
 *         </li>
 *         <li>
 * When a timeout occurs, the default behavior is to return null from the method. This can be changed by declaring a TimeoutException. If this exception is declared, a TimeoutException is thrown instead.
 *         </li>
 *         <li>
 * When a Thread is interrupted while waiting for a result, the default behavior is to return null. In that case, the interrupted flag is set back on the Thread. By declaring an InterruptedException on the method, this behavior is changed to throw that exception instead. The interrupt flag is removed when the exception is thrown, consistent with the java specification.
 *         </li>
 *         <li>
 * Other Runtime Exceptions may be declared on the method, but will not have any effect other than clarification to the API user.
 *         </li>
 *     </ul>
 *
 * </p>
 * <p>
 *     Timeout:
 *     <hr>
 *
 *     Methods annotated with @Timeout will block at most the indicated amount of time. This annotation is ignored if the method declares timeout parameters.
 * </p>
 */
public interface CommandGateway {

    int DEFAULT_TIMEOUT_SEC = 60;

    /**
     * Fire an forget.
     * Execute your command in another thread but with a timeout of 1 second.
     *
     * @param command a command
     * @param context a execution context
     * @throws java.lang.NullPointerException if command is null
     */
    void sendCommand(@NotNull Command command, @MetaData(Context.METANAME) Context context) throws Exception;

    /**
     * Fire and get a Future a command.
     * The Future object is returned immediatement or within 1 second(if Axon makes something weird).
     *
     * This method should return a Future immediately
     * for more details : <br>
     * http://www.axonframework.org/docs/2.0/command-handling.html <br>
     * Navigate to 3.1.2<br>.
     *
     * @param command a command
     * @param context a execution context
     * @throws java.lang.NullPointerException if command is null
     */
    Future<CommandResponse> sendCommandForFuture(@NotNull Command command, @MetaData(Context.METANAME) Context context) throws Exception;


    /**
     * Wait for command execution. Block a thread during all this time.
     * Be careful with your timeout value !!!
     * You can bloat the system if you block all the threads !!!
     * You will got a warning message if you try to set more than 10 minutes.
     *
     * @param command a command
     * @param context a execution context
     * @throws java.lang.NullPointerException if command is null
     */
    void sendCommandAndWait(@NotNull Command command, @MetaData(Context.METANAME) Context context, long timeout, TimeUnit unit) throws Exception;

    /**
     * Wait for response
     * @throws java.lang.NullPointerException if command is null
     * @see CommandGateway#sendCommandForFuture(Command, com.viadeo.kasper.context.Context)
     */
    @Deprecated
    @Timeout(value = DEFAULT_TIMEOUT_SEC, unit = TimeUnit.SECONDS)
    CommandResponse sendCommandAndWaitForAResponse(@NotNull Command command, @MetaData(Context.METANAME) Context context)
            throws Exception;

    /**
     * Wait for response and get exceptions
     * @throws java.lang.NullPointerException if command is null
     * @see CommandGateway#sendCommandForFuture(Command, com.viadeo.kasper.context.Context)
     */
    @Deprecated
    @Timeout(value = DEFAULT_TIMEOUT_SEC, unit = TimeUnit.SECONDS)
    CommandResponse sendCommandAndWaitForAResponseWithException(@NotNull Command command, @MetaData(Context.METANAME) Context context)
            throws Exception;

    /**
     * Register a command handler to the gateway
     *
     * @param commandHandler the command handler to be registered
     */
    void register(final CommandHandler commandHandler);

    /**
     * Register an interceptor factory to the gateway
     *
     * @param interceptorFactory the interceptor factory to register
     */
    void register(InterceptorFactory interceptorFactory);
}
