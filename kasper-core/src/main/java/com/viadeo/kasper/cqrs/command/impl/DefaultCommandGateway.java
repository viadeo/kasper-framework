package com.viadeo.kasper.cqrs.command.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.locators.impl.DefaultDomainLocator;
import com.viadeo.kasper.core.resolvers.*;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.ddd.repository.Repository;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.common.annotation.MetaData;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class DefaultCommandGateway implements CommandGateway {

    private final CommandGateway commandGateway;
    private final CommandBus commandBus;
    private final DomainLocator domainLocator;

    public DefaultCommandGateway(CommandBus commandBus) throws Exception {
        this(
                new CommandGatewayFactoryBean<CommandGateway>()
                , commandBus
                , new DefaultDomainLocator(
                    new RepositoryResolver(new EntityResolver(new ConceptResolver(), new RelationResolver(new ConceptResolver()))),
                    new CommandHandlerResolver()
                )
        );
    }

    protected DefaultCommandGateway(CommandGatewayFactoryBean<CommandGateway> commandGatewayFactoryBean, CommandBus commandBus, DomainLocator domainLocator) throws Exception {
        this.commandBus = Preconditions.checkNotNull(commandBus);
        this.domainLocator = Preconditions.checkNotNull(domainLocator);
        Preconditions.checkNotNull(commandGatewayFactoryBean);

        commandGatewayFactoryBean.setCommandBus(commandBus);
        commandGatewayFactoryBean.setGatewayInterface(CommandGateway.class);
        this.commandGateway = commandGatewayFactoryBean.getObject();
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

        Class<? extends CommandHandler> commandClass = commandHandler.getClass();

        domainLocator.registerHandler(commandHandler);

        // TODO the domain locator shouldn't be used outside of this implementation
        commandHandler.setDomainLocator(domainLocator);

        //- Dynamic type command class and command handler for Axon -------
        final AxonCommandCastor<Command> castor = new AxonCommandCastor<>(commandClass, commandHandler);
        commandBus.subscribe(castor.getBeanClass().getName(), castor.getContainerClass());
    }

    //TODO remove the bellow register method
    public void register(Repository repository) {
        domainLocator.registerRepository(Preconditions.checkNotNull(repository));
    }

    /**
     *
     * Convenient class for Axon command bus subscription proper dynamic typing
     *
     * @param <C> the Kasper command type handled
     */
    private static class AxonCommandCastor<C extends Command> {

        private final transient Class<? extends C> result;
        private final transient org.axonframework.commandhandling.CommandHandler handler;

        @SuppressWarnings("unchecked") // Safe by previous parent class typing
        AxonCommandCastor(final Class bean, final org.axonframework.commandhandling.CommandHandler container) {
            this.result = (Class<? extends C>) bean;
            this.handler = container;
        }

        public Class<? extends C> getBeanClass() {
            return this.result;
        }

        public org.axonframework.commandhandling.CommandHandler getContainerClass() {
            return this.handler;
        }
    }
}
