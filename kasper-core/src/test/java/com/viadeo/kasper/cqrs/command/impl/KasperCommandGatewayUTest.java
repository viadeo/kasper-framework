// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.impl;

import com.google.common.collect.Lists;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.interceptor.KasperCommandInterceptor;
import org.axonframework.commandhandling.CommandHandlerInterceptor;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

public class KasperCommandGatewayUTest {

    private KasperCommandGateway commandGateway;
    private KasperCommandBus commandBus;
    private CommandGatewayFactoryBean commandGatewayFactoryBean;
    private DomainLocator domainLocator;
    private CommandGateway decoratedCommandGateway;

    private InterceptorChainRegistry<Command, CommandResponse> interceptorChainRegistry;

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public KasperCommandGatewayUTest() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        commandGatewayFactoryBean = mock(CommandGatewayFactoryBean.class);
        decoratedCommandGateway = mock(CommandGateway.class);
        when(commandGatewayFactoryBean.getObject()).thenReturn(decoratedCommandGateway);
        commandBus = mock(KasperCommandBus.class);
        domainLocator = mock(DomainLocator.class);
        interceptorChainRegistry = mock(InterceptorChainRegistry.class);
        commandGateway = new KasperCommandGateway(commandGatewayFactoryBean, commandBus, domainLocator, interceptorChainRegistry);
    }

    // ------------------------------------------------------------------------

    @Test
    public void sendCommand_shouldDelegateTheCall() throws Exception {
        // Given
        final Command command = mock(Command.class);
        final Context context = mock(Context.class);

        // When
        commandGateway.sendCommand(command, context);

        // Then
        verify(decoratedCommandGateway).sendCommand(refEq(command), refEq(context));

    }

    @Test
    public void sendCommandForFuture_shouldDelegateTheCall() throws Exception {
        // Given
        final Command command = mock(Command.class);
        final Context context = mock(Context.class);

        // When
        commandGateway.sendCommandForFuture(command, context);

        // Then
        verify(decoratedCommandGateway).sendCommandForFuture(refEq(command), refEq(context));
    }

    @Test
    public void sendCommandAndWaitForAResponse_shouldDelegateTheCall() throws Exception {
        // Given
        final Command command = mock(Command.class);
        final Context context = mock(Context.class);

        // When
        commandGateway.sendCommandAndWaitForAResponse(command, context);

        // Then
        verify(decoratedCommandGateway).sendCommandAndWaitForAResponse(refEq(command), refEq(context));
    }

    @Test
    public void sendCommandAndWaitForAResponseWithException_shouldDelegateTheCall() throws Exception {
        // Given
        final Command command = mock(Command.class);
        final Context context = mock(Context.class);

        // When
        commandGateway.sendCommandAndWaitForAResponseWithException(command, context);

        // Then
        verify(decoratedCommandGateway).sendCommandAndWaitForAResponseWithException(refEq(command), refEq(context));
    }

    @Test
    public void sendCommandAndWait_shouldDelegateTheCall() throws Exception {
        // Given
        final Command command = mock(Command.class);
        final Context context = mock(Context.class);
        final TimeUnit unit = mock(TimeUnit.class);

        // When
        commandGateway.sendCommandAndWait(command, context, 1000, unit);

        // Then
        verify(decoratedCommandGateway).sendCommandAndWait(refEq(command), refEq(context), anyLong(), any(TimeUnit.class));
    }

    @Test(expected = NullPointerException.class)
    public void register_withNullAsCommandHandler_shouldThrownException() {
        // Given
        final CommandHandler commandHandler = null;

        // When
        commandGateway.register(commandHandler);

        // Then throws an exception
    }

    @Test
    public void register_withCommandHandler_shouldBeRegistered() {
        // Given
        final CommandHandler commandHandler = mock(CommandHandler.class);
        when(commandHandler.getCommandClass()).thenReturn(Command.class);

        // When
        commandGateway.register(commandHandler);

        // Then
        verify(domainLocator).registerHandler(refEq(commandHandler));
        verifyNoMoreInteractions(domainLocator);

        ArrayList<CommandHandlerInterceptor> handlerInterceptors = Lists.<CommandHandlerInterceptor>newArrayList(mock(KasperCommandInterceptor.class));
        verify(commandBus).subscribe(refEq(Command.class.getName()), any(org.axonframework.commandhandling.CommandHandler.class));
        verify(commandBus).setHandlerInterceptors(refEq(handlerInterceptors));
        verifyNoMoreInteractions(commandBus);

        verify(commandHandler).setCommandGateway(refEq(commandGateway));
        verify(commandHandler).getCommandClass();
        verifyNoMoreInteractions(commandHandler);

        verify(interceptorChainRegistry).create(eq(Command.class), any(CommandInterceptorFactory.class));
        verifyNoMoreInteractions(interceptorChainRegistry);
    }

}
