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
import com.viadeo.kasper.core.context.CurrentContext;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.unitofwork.UnitOfWork;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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
        assertTrue(CurrentContext.value().isPresent());
        assertEquals(context, CurrentContext.value().get());
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
