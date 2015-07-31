// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.gateway;

import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.KasperCommandBus;
import com.viadeo.kasper.core.component.command.interceptor.CommandHandlerInterceptorFactory;
import com.viadeo.kasper.core.component.command.interceptor.KasperCommandInterceptor;
import com.viadeo.kasper.core.context.CurrentContext;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import com.viadeo.kasper.core.interceptor.InterceptorFactory;
import com.viadeo.kasper.core.locators.DefaultDomainLocator;
import com.viadeo.kasper.core.locators.DomainLocator;
import org.axonframework.commandhandling.CommandDispatchInterceptor;
import org.axonframework.commandhandling.CommandHandlerInterceptor;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.common.annotation.MetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperCommandGateway implements CommandGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperCommandGateway.class);

    private final CommandGateway commandGateway;
    private final KasperCommandBus commandBus;
    private final DomainLocator domainLocator;
    private final InterceptorChainRegistry<Command, CommandResponse> interceptorChainRegistry;

    // ------------------------------------------------------------------------

    public KasperCommandGateway(final KasperCommandBus commandBus) {
        this(
             new CommandGatewayFactoryBean<CommandGateway>(),
             checkNotNull(commandBus),
             new DefaultDomainLocator(),
             new InterceptorChainRegistry<Command, CommandResponse>()
        );
    }

    public KasperCommandGateway(final KasperCommandBus commandBus,
                                final CommandDispatchInterceptor... commandDispatchInterceptors) {
        this(
            new CommandGatewayFactoryBean<CommandGateway>(),
            checkNotNull(commandBus),
            new DefaultDomainLocator(),
            new InterceptorChainRegistry<Command, CommandResponse>(),
            checkNotNull(commandDispatchInterceptors)
        );
    }

    protected KasperCommandGateway(final CommandGatewayFactoryBean<CommandGateway> commandGatewayFactoryBean,
                                   final KasperCommandBus commandBus,
                                   final DomainLocator domainLocator,
                                   final InterceptorChainRegistry<Command, CommandResponse> interceptorChainRegistry,
                                   final CommandDispatchInterceptor... commandDispatchInterceptors) {

        this.commandBus = checkNotNull(commandBus);
        this.domainLocator = checkNotNull(domainLocator);
        this.interceptorChainRegistry = checkNotNull(interceptorChainRegistry);

        checkNotNull(commandGatewayFactoryBean);
        checkNotNull(commandDispatchInterceptors);

        commandGatewayFactoryBean.setCommandBus(commandBus);
        commandGatewayFactoryBean.setGatewayInterface(CommandGateway.class);
        commandGatewayFactoryBean.setCommandDispatchInterceptors(Lists.newArrayList(commandDispatchInterceptors));

        this.commandBus.setHandlerInterceptors(Lists.<CommandHandlerInterceptor>newArrayList(
            new KasperCommandInterceptor(commandBus, interceptorChainRegistry)
        ));

        try {
            commandGatewayFactoryBean.afterPropertiesSet();
        } catch (final Exception e) {
            throw new KasperException("Unable to bind Axon Command Gateway", e);
        }

        try {
            this.commandGateway = checkNotNull(commandGatewayFactoryBean.getObject());
        } catch (final Exception e) {
            throw new KasperException("Unable to initialize the Command Gateway", e);
        }
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void sendCommand(
                final Command command,
                @MetaData(Context.METANAME)
                final Context context) {
        checkNotNull(command);
        checkNotNull(context);

        CurrentContext.set(context);

        commandGateway.sendCommand(
                command,
                context
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public Future<CommandResponse> sendCommandForFuture(
                final Command command,
                @MetaData(Context.METANAME)
                final Context context) throws Exception {
        checkNotNull(command);
        checkNotNull(context);

        CurrentContext.set(context);

        return commandGateway.sendCommandForFuture(
                command,
                context
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResponse sendCommandAndWaitForAResponse(
                final Command command,
                @MetaData(Context.METANAME)
                final Context context) throws Exception {
        checkNotNull(command);
        checkNotNull(context);

        CurrentContext.set(context);

        return commandGateway.sendCommandAndWaitForAResponse(
                command,
                context
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResponse sendCommandAndWaitForAResponseWithException(
                final Command command,
                @MetaData(Context.METANAME)
                final Context context) throws Exception {
        checkNotNull(command);
        checkNotNull(context);

        CurrentContext.set(context);

        return commandGateway.sendCommandAndWaitForAResponseWithException(
                command,
                context
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void sendCommandAndWait(
                final Command command,
                @MetaData(Context.METANAME)
                final Context context,
                final long timeout,
                final TimeUnit unit) throws Exception {
        checkNotNull(command);
        checkNotNull(context);
        checkNotNull(unit);

        CurrentContext.set(context);

        commandGateway.sendCommandAndWait(
                command,
                context,
                timeout,
                unit
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void sendCommandAndWaitForever(
                final Command command,
                @MetaData(Context.METANAME)
                final Context context) throws Exception {
        checkNotNull(command);
        checkNotNull(context);

        CurrentContext.set(context);

        commandGateway.sendCommandAndWaitForever(
                command,
                context
        );
    }

    // ------------------------------------------------------------------------

    /**
     * Register a command handler to the gateway
     *
     * @param commandHandler the command handler to be registered
     * @param <COMMAND> the command
     */
    public <COMMAND extends Command> void register(final CommandHandler<COMMAND> commandHandler) {

        domainLocator.registerHandler(checkNotNull(commandHandler));

        commandBus.subscribe(
                commandHandler.getInputClass().getName(),
                new AxonCommandHandler<>(commandHandler)
        );

        // create immediately the interceptor chain instead of lazy mode
        interceptorChainRegistry.create(
                commandHandler.getHandlerClass(),
                new CommandHandlerInterceptorFactory()
        );
    }

    /**
     * Register an interceptor factory to the gateway
     *
     * @param interceptorFactory the query interceptor factory to register
     */
    public void register(final InterceptorFactory interceptorFactory) {
        checkNotNull(interceptorFactory);
        LOGGER.info("Registering the query interceptor factory : " + interceptorFactory.getClass().getSimpleName());

        interceptorChainRegistry.register(interceptorFactory);
    }

}
