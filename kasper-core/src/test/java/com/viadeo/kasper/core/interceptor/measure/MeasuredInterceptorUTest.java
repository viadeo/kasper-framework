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
package com.viadeo.kasper.core.interceptor.measure;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.gateway.AxonCommandHandler;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.core.component.command.interceptor.CommandHandlerInterceptorFactory;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import org.axonframework.unitofwork.CurrentUnitOfWork;
import org.axonframework.unitofwork.DefaultUnitOfWork;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class MeasuredInterceptorUTest {

    private MetricRegistry metricRegistry;
    private InterceptorChain<Command, CommandResponse> interceptorChain;
    private CommandResponse response;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        Timer timer = mock(Timer.class);
        when(timer.time()).thenReturn(mock(Timer.Context.class));

        metricRegistry = mock(MetricRegistry.class);
        when(metricRegistry.meter(anyString())).thenReturn(mock(Meter.class));
        when(metricRegistry.timer(anyString())).thenReturn(timer);

        CurrentUnitOfWork.set(new DefaultUnitOfWork());

        InterceptorChainRegistry<Command, CommandResponse> chainRegistry = new InterceptorChainRegistry<>();
        chainRegistry.register(new MeasuredInterceptor.Factory(CommandGateway.class, metricRegistry));
        interceptorChain = chainRegistry.create(
                CommandHandler.class,
                new CommandHandlerInterceptorFactory(new AxonCommandHandler<>(new CommandHandler<Command>() {

                    @Override
                    public CommandResponse handle(com.viadeo.kasper.core.component.command.CommandMessage message) {
                        return response;
                    }

                    @Override
                    public Class<Command> getInputClass() {
                        return Command.class;
                    }

                    @Override
                    public Class<? extends CommandHandler> getHandlerClass() {
                        return CommandHandler.class;
                    }
                }))
        ).get();
    }

    @Test
    public void proceed_an_interceptor_chain_with_ok_as_response() throws Exception {
        // When
        response = CommandResponse.ok();
        Object input = interceptorChain.next(mock(Command.class), Contexts.empty());

        // Then
        assertNotNull(input);
        verify(metricRegistry).timer("com.viadeo.kasper.core.component.command.gateway.commandgateway.requests-handle-time");
        verifyNoMoreInteractions(metricRegistry);
    }

    @Test
    public void proceed_an_interceptor_chain_with_error_as_response() throws Exception {
        // When
        response = CommandResponse.error(CoreReasonCode.UNKNOWN_REASON);
        Object input = interceptorChain.next(mock(Command.class), Contexts.empty());

        // Then
        assertNotNull(input);
        verify(metricRegistry).meter("com.viadeo.kasper.core.component.command.gateway.commandgateway.errors");
        verify(metricRegistry).timer("com.viadeo.kasper.core.component.command.gateway.commandgateway.requests-handle-time");
        verifyNoMoreInteractions(metricRegistry);
    }


}
