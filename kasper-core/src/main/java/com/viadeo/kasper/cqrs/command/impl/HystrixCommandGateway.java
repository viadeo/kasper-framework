package com.viadeo.kasper.cqrs.command.impl;

import com.codahale.metrics.MetricRegistry;
import com.google.common.util.concurrent.Futures;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandProperties;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.resilience.HystrixCommandWithExceptionPolicy;
import com.viadeo.kasper.resilience.HystrixGateway;
import com.viadeo.kasper.resilience.HystrixHelper;
import org.axonframework.common.annotation.MetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 * Decorator for axon command gateway proxy.
 * Used by KasperCommandGateway.<br>
 *
 * Add Hystrix features :
 * <ul>
 *     <li>circuit breaker</li>
 *     <li>thread pool isolation</li>
 *     <li>timeout on method</li>
 *     <li>fallbacks</li>
 * </ul>
 *
 * @see com.viadeo.kasper.cqrs.command.impl.KasperCommandGateway
 */
public class HystrixCommandGateway extends HystrixGateway implements CommandGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(HystrixCommandGateway.class);

    /**
     * -> at coding time = 1 minute(defined by kasper command gateway interface)
     */
    public static final int DEFAULT_TIMEOUT_IN_MS = (int) TimeUnit.MILLISECONDS.convert(CommandGateway.DEFAULT_TIMEOUT_SEC, TimeUnit.SECONDS);

    /**
     * -> 10 minutes, warn client if timeout asked is longer than this value
     */
    public static final long LONG_TIMEOUT_IN_MS = TimeUnit.MILLISECONDS.convert(10L, TimeUnit.MINUTES);

    private final CommandGateway commandGateway;

    public HystrixCommandGateway(CommandGateway commandGateway, MetricRegistry metricRegistry) {
        super(metricRegistry);
        this.commandGateway = commandGateway;
    }


    /**
     * {@inheritDoc}
     *
     * @param command a command a command to send to command handler
     * @param context a execution context an execution context
     * @throws java.lang.NullPointerException                           if command is null
     * @throws com.netflix.hystrix.exception.HystrixBadRequestException if an exception is thrown by an interceptor
     */
    @Override
    public void sendCommand(final @NotNull Command command, final @MetaData(Context.METANAME) Context context) {


        HystrixCommand<Void> commandHystrixCommand = new HystrixCommandWithExceptionPolicy<Void>(HystrixHelper.buildSetter(command)) {

            @Override
            protected Void runWithException() throws Exception {
                commandGateway.sendCommand(command, context);
                return null;
            }

            @Override
            protected Void getFallback() {
                reportFallback(command.getClass().getName());
                return null;
            }
        };
        commandHystrixCommand.execute();
    }

    /**
     * {@inheritDoc}
     *
     * @param command a command a command to send to command handler
     * @param context a execution context an execution context
     * @return a Future<CommandResponse> to be able to get the result later
     * @throws java.lang.NullPointerException                           if command is null
     * @throws com.netflix.hystrix.exception.HystrixBadRequestException if an exception is thrown by an interceptor
     */
    @Override
    public Future<CommandResponse> sendCommandForFuture(@NotNull final Command command, @MetaData(Context.METANAME) final Context context) {
        HystrixCommand<Future<CommandResponse>> commandHystrixCommand = new HystrixCommandWithExceptionPolicy<Future<CommandResponse>>(
                HystrixHelper.buildSetter(command)
                        .andCommandPropertiesDefaults(
                                HystrixCommandProperties.Setter()
                                        .withExecutionIsolationThreadTimeoutInMilliseconds(DEFAULT_TIMEOUT_IN_MS))) {

            @Override
            protected Future<CommandResponse> runWithException() throws Exception {
                return commandGateway.sendCommandForFuture(command, context);
            }

            @Override
            protected Future<CommandResponse> getFallback() {
                reportFallback(command.getClass().getName());
                return Futures.immediateFuture(CommandResponse.error(CoreReasonCode.INTERNAL_COMPONENT_TIMEOUT));
            }

        };
        return commandHystrixCommand.execute();
    }

    /**
     * {@inheritDoc}
     *
     * @param command a command a command to send to command handler
     * @param context a execution context an execution context
     * @param timeout a timeout value in 'unit'
     * @param unit    a TimeUnit
     * @throws java.lang.NullPointerException                           if command is null
     * @throws com.netflix.hystrix.exception.HystrixBadRequestException if an exception is thrown by an interceptor
     */
    @Override
    public void sendCommandAndWait(@NotNull final Command command, @MetaData(Context.METANAME) final Context context, long timeout, TimeUnit unit) {

        long timeoutInMs = TimeUnit.MILLISECONDS.convert(timeout, unit);

        if (timeoutInMs >= LONG_TIMEOUT_IN_MS) {
            LOGGER.warn("Timeout for sending command {} is too long !! Try to set it with a lower value(ex: less than 10 minutes)", command.getClass().getName());
        }

        HystrixCommandProperties.Setter confTimeout = HystrixCommandProperties.Setter()
                .withExecutionIsolationThreadTimeoutInMilliseconds((int) timeoutInMs);

        // add a specific timeout for this pool
        HystrixCommand<Void> commandHystrixCommand = new HystrixCommandWithExceptionPolicy<Void>(
                HystrixHelper.buildSetter(command)
                        .andCommandPropertiesDefaults(confTimeout)) {

            @Override
            protected Void runWithException() throws Exception {
                commandGateway.sendCommand(command, context);
                return null;
            }

            @Override
            protected Void getFallback() {
                reportFallback(command.getClass().getName());
                return null;
            }
        };
        commandHystrixCommand.execute();

    }

    @Override
    public CommandResponse sendCommandAndWaitForAResponse(@NotNull Command command, @MetaData(Context.METANAME) Context context) throws Exception {
        // no support
        return commandGateway.sendCommandAndWaitForAResponse(command, context);
    }

    @Override
    public CommandResponse sendCommandAndWaitForAResponseWithException(@NotNull Command command, @MetaData(Context.METANAME) Context context) throws Exception {
        return commandGateway.sendCommandAndWaitForAResponseWithException(command, context);
    }

}
