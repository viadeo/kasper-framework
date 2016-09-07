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

import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.CommandMessage;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.unitofwork.UnitOfWork;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class AxonCommandHandlerUTest {

    @Mock
    private UnitOfWork unitOfWork;

    @Mock
    private CommandHandler<Command> handler;

    private AxonCommandHandler<Command> axonHandler;

    @Before
    public void setUp() throws Exception {
        when(unitOfWork.isStarted()).thenReturn(Boolean.TRUE);
        when(handler.handle(any(CommandMessage.class))).thenReturn(CommandResponse.ok());
        axonHandler = new AxonCommandHandler<>(handler);
    }

    @Test
    public void handle_set_the_context_message_as_current() throws Throwable {
        // Given
        Context context = Contexts.builder()
                .with("key", "value")
                .with("key1", "value1")
                .with("key2", "value2")
                .build();
        GenericCommandMessage<Command> message = new GenericCommandMessage<>(
                mock(Command.class),
                context.asMetaDataMap()
        );

        // When
        Object handle = axonHandler.handle(message, unitOfWork);

        // Then
        assertNotNull(handle);
    }

    @Test
    public void rollback_an_unexpected_exception() throws Throwable {
        // Given
        RuntimeException toBeThrown = new RuntimeException("Fake!");
        doThrow(toBeThrown).when(handler).handle(any(CommandMessage.class));

        GenericCommandMessage<Command> message = new GenericCommandMessage<>(
                mock(Command.class),
                Contexts.empty().asMetaDataMap()
        );

        // When
        Object handle = null;
        Exception exception = null;

        try {
            handle = axonHandler.handle(message, unitOfWork);
        } catch (Exception e) {
            exception = e;
        }

        // Then
        assertNull(handle);
        assertNotNull(exception);
        verify(unitOfWork).rollback(eq(toBeThrown));
    }

    @Test
    public void rollback_a_refused_command() throws Throwable {
        // Given
        when(handler.handle(any(CommandMessage.class))).thenReturn(CommandResponse.refused(CoreReasonCode.UNKNOWN_REASON));

        GenericCommandMessage<Command> message = new GenericCommandMessage<>(
                mock(Command.class),
                Contexts.empty().asMetaDataMap()
        );

        // When
        Object handle = axonHandler.handle(message, unitOfWork);

        // Then
        assertNotNull(handle);
        verify(unitOfWork).rollback();
    }

    @Test
    public void rollback_an_error_command() throws Throwable {
        when(handler.handle(any(CommandMessage.class))).thenReturn(CommandResponse.error(CoreReasonCode.UNKNOWN_REASON));

        GenericCommandMessage<Command> message = new GenericCommandMessage<>(
                mock(Command.class),
                Contexts.empty().asMetaDataMap()
        );

        // When
        Object handle = axonHandler.handle(message, unitOfWork);

        // Then
        assertNotNull(handle);
        verify(unitOfWork).rollback();
    }

}
