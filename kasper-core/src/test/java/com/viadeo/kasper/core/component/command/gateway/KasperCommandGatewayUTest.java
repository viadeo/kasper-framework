// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.gateway;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.MeasuredCommandHandler;
import com.viadeo.kasper.core.component.command.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.component.command.interceptor.KasperCommandInterceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import com.viadeo.kasper.core.interceptor.InterceptorFactory;
import com.viadeo.kasper.core.locators.DomainLocator;
import org.axonframework.commandhandling.CommandHandlerInterceptor;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.refEq;
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
        when(domainLocator.getHandlerForCommandClass(ArgumentMatchers.<Class<Command>>any()))
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
        final AutowiredCommandHandler commandHandler = mock(AutowiredCommandHandler.class);
        when(commandHandler.getInputClass()).thenReturn(Command.class);
        when(commandHandler.getHandlerClass()).thenReturn(AutowiredCommandHandler.class);

        // When
        commandGateway.register(commandHandler);

        // Then
        verify(domainLocator).registerHandler(refEq(commandHandler));
        verifyNoMoreInteractions(domainLocator);

        ArrayList<CommandHandlerInterceptor> handlerInterceptors = Lists.<CommandHandlerInterceptor>newArrayList(mock(KasperCommandInterceptor.class));
        verify(commandBus).subscribe(refEq(Command.class.getName()), any(org.axonframework.commandhandling.CommandHandler.class));
        verify(commandBus).setHandlerInterceptors(refEq(handlerInterceptors));
        verifyNoMoreInteractions(commandBus);

        verify(commandHandler).getInputClass();

        verify(interceptorChainRegistry).create(eq(AutowiredCommandHandler.class), any(CommandInterceptorFactory.class));
        verifyNoMoreInteractions(interceptorChainRegistry);
    }

    @Test
    public void register_should_create_interceptor_with_the_command_handler_class() {
        // Given
        final AutowiredCommandHandler commandHandler = mock(AutowiredCommandHandler.class);
        when(commandHandler.getInputClass()).thenReturn(Command.class);
        when(commandHandler.getHandlerClass()).thenReturn(AutowiredCommandHandler.class);

        final CommandHandler measuredCommandHandler = new MeasuredCommandHandler(new MetricRegistry(), commandHandler);

        // When
        commandGateway.register(measuredCommandHandler);

        // Then
        verify(interceptorChainRegistry).create(eq(AutowiredCommandHandler.class), any(InterceptorFactory.class));
    }
}
