// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.impl;

import com.google.common.collect.Lists;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import com.viadeo.kasper.core.interceptor.InterceptorFactory;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.locators.impl.DefaultDomainLocator;
import com.viadeo.kasper.core.resolvers.CommandHandlerResolver;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.interceptor.CommandHandlerInterceptorFactory;
import com.viadeo.kasper.cqrs.command.interceptor.KasperCommandInterceptor;
import com.viadeo.kasper.exception.KasperException;
import org.axonframework.commandhandling.CommandDispatchInterceptor;
import org.axonframework.commandhandling.CommandHandlerInterceptor;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.common.annotation.MetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

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

    /**
     *
     * Convenient class for Axon command bus subscription proper dynamic typing
     *
     * @param <C> the Kasper command type handled
     */
    private static class AxonCommandCastor<C extends Command> {

        private final transient Class<? extends C> commandClass;
        private final transient org.axonframework.commandhandling.CommandHandler handler;

        @SuppressWarnings("unchecked") // Safe by previous parent class typing
        AxonCommandCastor(final Class commandClass, final org.axonframework.commandhandling.CommandHandler container) {
            this.commandClass = (Class<? extends C>) commandClass;
            this.handler = container;
        }

        public Class<? extends C> getBeanClass() {
            return this.commandClass;
        }

        public org.axonframework.commandhandling.CommandHandler getContainerClass() {
            return this.handler;
        }
    }

    // ------------------------------------------------------------------------

    public KasperCommandGateway(final KasperCommandBus commandBus) {
        this(
             new CommandGatewayFactoryBean<CommandGateway>(),
             checkNotNull(commandBus),
             new DefaultDomainLocator(new CommandHandlerResolver()),
             new InterceptorChainRegistry<Command, CommandResponse>()
        );
    }

    public KasperCommandGateway(final KasperCommandBus commandBus,
                                final CommandDispatchInterceptor... commandDispatchInterceptors) {
        this(
            new CommandGatewayFactoryBean<CommandGateway>(),
            checkNotNull(commandBus),
            new DefaultDomainLocator(new CommandHandlerResolver()),
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

        addContextToMDC(context);

        commandGateway.sendCommand(
                checkNotNull(command),
                context
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public Future<CommandResponse> sendCommandForFuture(
                final Command command,
                @MetaData(Context.METANAME)
                final Context context) throws Exception {

        addContextToMDC(context);

        return commandGateway.sendCommandForFuture(
                checkNotNull(command),
                context
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResponse sendCommandAndWaitForAResponse(
                final Command command,
                @MetaData(Context.METANAME)
                final Context context) throws Exception {

        addContextToMDC(context);

        return commandGateway.sendCommandAndWaitForAResponse(
                checkNotNull(command),
                context
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResponse sendCommandAndWaitForAResponseWithException(
                final Command command,
                @MetaData(Context.METANAME)
                final Context context) throws Exception {

        addContextToMDC(context);

        return commandGateway.sendCommandAndWaitForAResponseWithException(
                checkNotNull(command),
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

        addContextToMDC(context);

        commandGateway.sendCommandAndWait(
                checkNotNull(command),
                context,
                timeout,
                checkNotNull(unit)
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void sendCommandAndWaitForever(
                final Command command,
                @MetaData(Context.METANAME)
                final Context context) throws Exception {

        addContextToMDC(context);

        commandGateway.sendCommandAndWaitForever(
                checkNotNull(command),
                context
        );
    }

    private static void addContextToMDC(Context context) {
        MDC.setContextMap(checkNotNull(context).asMap(MDC.getCopyOfContextMap()));
    }

    // ------------------------------------------------------------------------

    /**
     * Register a command handler to the gateway
     *
     * @param commandHandler the command handler to be registered
     */
    public void register(final CommandHandler commandHandler) {

        domainLocator.registerHandler(checkNotNull(commandHandler));

        final Class commandClass = commandHandler.getCommandClass();

        //- Dynamic type command class and command handler for Axon -------
        final AxonCommandCastor<Command> castor = new AxonCommandCastor<>(
            commandClass,
            commandHandler
        );

        commandBus.subscribe(
                castor.getBeanClass().getName(),
                castor.getContainerClass()
        );

        commandHandler.setCommandGateway(this);

        // create immediately the interceptor chain instead of lazy mode
        interceptorChainRegistry.create(
                commandHandler.getClass(),
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
