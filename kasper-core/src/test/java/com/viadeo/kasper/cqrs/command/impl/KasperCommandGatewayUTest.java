// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.impl;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.core.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.api.domain.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.api.domain.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.interceptor.KasperCommandInterceptor;
import org.axonframework.commandhandling.CommandHandlerInterceptor;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

public class KasperCommandGatewayUTest {

    private final KasperCommandGateway commandGateway;
    private final KasperCommandBus commandBus;
    private final DomainLocator domainLocator;
    private final CommandGateway decoratedCommandGateway;
    private final InterceptorChainRegistry<Command, CommandResponse> interceptorChainRegistry;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public KasperCommandGatewayUTest() throws Exception {
        final CommandGatewayFactoryBean<CommandGateway> commandGatewayFactoryBean = mock(CommandGatewayFactoryBean.class);
        decoratedCommandGateway = mock(CommandGateway.class);
        when(commandGatewayFactoryBean.getObject()).thenReturn(decoratedCommandGateway);
        commandBus = mock(KasperCommandBus.class);
        domainLocator = mock(DomainLocator.class);
        interceptorChainRegistry = mock(InterceptorChainRegistry.class);
        commandGateway = new KasperCommandGateway(commandGatewayFactoryBean, commandBus, domainLocator, interceptorChainRegistry);
    }

    @Before
    public void setUp() {
        reset(domainLocator, decoratedCommandGateway);
        when(domainLocator.getHandlerForCommandClass(Matchers.<Class<Command>>any()))
                .thenReturn(Optional.<CommandHandler>absent());
        MDC.clear();
    }

    // ------------------------------------------------------------------------

    @Test
    public void sendCommand_shouldDelegateTheCall() throws Exception {
        // Given
        final Command command = mock(Command.class);
        final Context context = Contexts.empty();

        // When
        commandGateway.sendCommand(command, context);

        // Then
        verify(decoratedCommandGateway).sendCommand(refEq(command), eq(context));
        verifyNoMoreInteractions(decoratedCommandGateway);
    }

    @Test
    public void sendCommandForFuture_shouldDelegateTheCall() throws Exception {
        // Given
        final Command command = mock(Command.class);
        final Context context = Contexts.empty();

        // When
        commandGateway.sendCommandForFuture(command, context);

        // Then
        verify(decoratedCommandGateway).sendCommandForFuture(refEq(command), eq(context));
        verifyNoMoreInteractions(decoratedCommandGateway);
    }

    @Test
    public void sendCommandAndWaitForAResponse_shouldDelegateTheCall() throws Exception {
        // Given
        final Command command = mock(Command.class);
        final Context context = Contexts.empty();

        // When
        commandGateway.sendCommandAndWaitForAResponse(command, context);

        // Then
        verify(decoratedCommandGateway).sendCommandAndWaitForAResponse(refEq(command), eq(context));
        verifyNoMoreInteractions(decoratedCommandGateway);
    }

    @Test
    public void sendCommandAndWaitForAResponseWithException_shouldDelegateTheCall() throws Exception {
        // Given
        final Command command = mock(Command.class);
        final Context context = Contexts.empty();

        // When
        commandGateway.sendCommandAndWaitForAResponseWithException(command, context);

        // Then
        verify(decoratedCommandGateway).sendCommandAndWaitForAResponseWithException(refEq(command), eq(context));
        verifyNoMoreInteractions(decoratedCommandGateway);
    }

    @Test
    public void sendCommandAndWait_shouldDelegateTheCall() throws Exception {
        // Given
        final Command command = mock(Command.class);
        final Context context = Contexts.empty();
        final TimeUnit unit = mock(TimeUnit.class);

        // When
        commandGateway.sendCommandAndWait(command, context, 1000, unit);

        // Then
        verify(decoratedCommandGateway).sendCommandAndWait(refEq(command), eq(context), anyLong(), refEq(unit));
        verifyNoMoreInteractions(decoratedCommandGateway);
    }

    @Test
    public void sendCommandAndWaitForever_shouldDelegateTheCall() throws Exception {
        // Given
        final Command command = mock(Command.class);
        final Context context = Contexts.empty();

        // When
        commandGateway.sendCommandAndWaitForever(command, context);

        // Then
        verify(decoratedCommandGateway).sendCommandAndWaitForever(refEq(command), refEq(context));
        verifyNoMoreInteractions(decoratedCommandGateway);
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

        verify(interceptorChainRegistry).create(eq(commandHandler.getClass()), any(CommandInterceptorFactory.class));
        verifyNoMoreInteractions(interceptorChainRegistry);
    }
}
