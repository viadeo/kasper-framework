package com.viadeo.kasper.cqrs.command.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.locators.impl.DefaultDomainLocator;
import com.viadeo.kasper.core.resolvers.CommandHandlerResolver;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.exception.KasperException;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.CommandDispatchInterceptor;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.commandhandling.interceptors.BeanValidationInterceptor;
import org.axonframework.common.annotation.MetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.ValidationException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class DefaultCommandGateway implements CommandGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCommandGateway.class);

    private final CommandGateway commandGateway;
    private final CommandBus commandBus;
    private final DomainLocator domainLocator;

    public DefaultCommandGateway(CommandBus commandBus) {
        this(
                new CommandGatewayFactoryBean<CommandGateway>()
                , commandBus
                , new DefaultDomainLocator(new CommandHandlerResolver())
        );
    }

    protected DefaultCommandGateway(CommandGatewayFactoryBean<CommandGateway> commandGatewayFactoryBean, CommandBus commandBus, DomainLocator domainLocator) {
        this.commandBus = Preconditions.checkNotNull(commandBus);
        this.domainLocator = Preconditions.checkNotNull(domainLocator);
        Preconditions.checkNotNull(commandGatewayFactoryBean);

        commandGatewayFactoryBean.setCommandBus(commandBus);
        commandGatewayFactoryBean.setGatewayInterface(CommandGateway.class);

        try {
            commandGatewayFactoryBean.setCommandDispatchInterceptors(
                    Lists.<CommandDispatchInterceptor>newArrayList(
                            new BeanValidationInterceptor(Validation.buildDefaultValidatorFactory())
                    )
            );
        } catch (final ValidationException ve) {
            LOGGER.warn("No implementation found for BEAN VALIDATION - JSR 303" , ve);
        }

        try {
            commandGatewayFactoryBean.afterPropertiesSet();
        } catch (final Exception e) {
            throw new KasperException("Unable to bind Axon Command Gateway", e);
        }

        try {
            this.commandGateway = Preconditions.checkNotNull(commandGatewayFactoryBean.getObject());
        } catch (Exception e) {
            throw new KasperException("Unable to initialize the Command Gateway", e);
        }
    }

    @Override
    public void sendCommand(Command command, @MetaData(Context.METANAME) Context context) throws Exception {
        commandGateway.sendCommand(command, context);
    }

    @Override
    public Future<CommandResponse> sendCommandForFuture(Command command, @MetaData(Context.METANAME) Context context) throws Exception {
        return commandGateway.sendCommandForFuture(command, context);
    }

    @Override
    public CommandResponse sendCommandAndWaitForAResponse(Command command, @MetaData(Context.METANAME) Context context) throws Exception {
        return commandGateway.sendCommandAndWaitForAResponse(command, context);
    }

    @Override
    public CommandResponse sendCommandAndWaitForAResponseWithException(Command command, @MetaData(Context.METANAME) Context context) throws Exception {
        return commandGateway.sendCommandAndWaitForAResponseWithException(command, context);
    }

    @Override
    public void sendCommandAndWait(Command command, @MetaData(Context.METANAME) Context context, long timeout, TimeUnit unit) throws Exception {
        commandGateway.sendCommandAndWait(command, context, timeout, unit);
    }

    @Override
    public void sendCommandAndWaitForever(Command command, @MetaData(Context.METANAME) Context context) throws Exception {
        commandGateway.sendCommandAndWaitForever(command, context);
    }

    public void register(CommandHandler commandHandler) {
        Preconditions.checkNotNull(commandHandler);

        domainLocator.registerHandler(commandHandler);

        //- Dynamic type command class and command handler for Axon -------
        final AxonCommandCastor<Command> castor = new AxonCommandCastor<>(
                  commandHandler.getCommandClass()
                , commandHandler
        );
        commandBus.subscribe(castor.getBeanClass().getName(), castor.getContainerClass());
    }

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
}
