package com.viadeo.kasper.cqrs.command.impl;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;

public class DefaultCommandGatewayUTest {

    private final DefaultCommandGateway commandGateway;
    private final CommandBus commandBus;
    private final DomainLocator domainLocator;
    private final CommandGateway decoratedCommandGateway;

    @SuppressWarnings("unchecked")
    public DefaultCommandGatewayUTest() throws Exception {
        CommandGatewayFactoryBean<CommandGateway> commandGatewayFactoryBean = mock(CommandGatewayFactoryBean.class);
        decoratedCommandGateway = mock(CommandGateway.class);
        when(commandGatewayFactoryBean.getObject()).thenReturn(decoratedCommandGateway);
        commandBus = mock(CommandBus.class);
        domainLocator = mock(DomainLocator.class);
        commandGateway = new DefaultCommandGateway(commandGatewayFactoryBean, commandBus, domainLocator);
    }

    @Before
    public void setUp(){
        reset(domainLocator, decoratedCommandGateway);
    }

    @Test
    public void sendCommand_shouldDelegateTheCall() throws Exception {
        // Given
        Command command = mock(Command.class);
        Context context = mock(Context.class);

        // When
        commandGateway.sendCommand(command, context);

        // Then
        verify(decoratedCommandGateway).sendCommand(refEq(command), refEq(context));
    }

    @Test
    public void sendCommandForFuture_shouldDelegateTheCall() throws Exception {
        // Given
        Command command = mock(Command.class);
        Context context = mock(Context.class);

        // When
        commandGateway.sendCommandForFuture(command, context);

        // Then
        verify(decoratedCommandGateway).sendCommandForFuture(refEq(command), refEq(context));
    }

    @Test
    public void sendCommandAndWaitForAResponse_shouldDelegateTheCall() throws Exception {
        // Given
        Command command = mock(Command.class);
        Context context = mock(Context.class);

        // When
        commandGateway.sendCommandAndWaitForAResponse(command, context);

        // Then
        verify(decoratedCommandGateway).sendCommandAndWaitForAResponse(refEq(command), refEq(context));
    }

    @Test
    public void sendCommandAndWaitForAResponseWithException_shouldDelegateTheCall() throws Exception {
        // Given
        Command command = mock(Command.class);
        Context context = mock(Context.class);

        // When
        commandGateway.sendCommandAndWaitForAResponseWithException(command, context);

        // Then
        verify(decoratedCommandGateway).sendCommandAndWaitForAResponseWithException(refEq(command), refEq(context));
    }

    @Test
    public void sendCommandAndWait_shouldDelegateTheCall() throws Exception {
        // Given
        Command command = mock(Command.class);
        Context context = mock(Context.class);
        TimeUnit unit = mock(TimeUnit.class);

        // When
        commandGateway.sendCommandAndWait(command, context, 1000, unit);

        // Then
        verify(decoratedCommandGateway).sendCommandAndWait(refEq(command), refEq(context), anyLong(), refEq(unit));
    }

    @Test
    public void sendCommandAndWaitForever() throws Exception {
        // Given
        Command command = mock(Command.class);
        Context context = mock(Context.class);

        // When
        commandGateway.sendCommandAndWaitForever(command, context);

        // Then
        verify(decoratedCommandGateway).sendCommandAndWaitForever(refEq(command), refEq(context));
    }

    @Test(expected = NullPointerException.class)
    public void register_withNullAsCommandHandler_shouldThrownException() {
        // Given
        CommandHandler commandHandler = null;

        // When
        commandGateway.register(commandHandler);

        // Then throws an exception
    }

    @Test
    public void register_withCommandHandler_shouldBeRegistered() {
        // Given
        CommandHandler commandHandler = mock(CommandHandler.class);
        when(commandHandler.getCommandClass()).thenReturn(Command.class);

        // When
        commandGateway.register(commandHandler);

        // Then
        verify(domainLocator).registerHandler(refEq(commandHandler));
        verify(commandBus).subscribe(refEq(Command.class.getName()), any(org.axonframework.commandhandling.CommandHandler.class));
    }
}
