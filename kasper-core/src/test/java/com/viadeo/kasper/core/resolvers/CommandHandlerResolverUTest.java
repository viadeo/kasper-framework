// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.api.domain.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CommandHandlerResolverUTest {

    CommandHandlerResolver resolver;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // ------------------------------------------------------------------------

    @XKasperUnregistered
    private static class TestCommand implements Command {
    }

    @XKasperUnregistered
    private static class TestCommandHandler extends CommandHandler<TestCommand> {
    }

    // ------------------------------------------------------------------------

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        resolver = new CommandHandlerResolver();
    }


    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("all")
    public void getHandlerClass_withNullCommand_shouldThrowNPE() {
        // Given
        final Class<? extends Command> command = null;

        // Expect
        thrown.expect(NullPointerException.class);

        // When
        resolver.getHandlerClass(command);
    }

    @Test
    @SuppressWarnings("all")
    public void getHandlerClass_withUnregisteredCommand_shouldReturnNull() {
        // Given
        final Class<? extends Command> command = TestCommand.class;

        // When
        final Optional<Class<? extends CommandHandler>> handlerClass = resolver.getHandlerClass(command);

        // Then
        assertFalse(handlerClass.isPresent());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getHandlerClass_withRegisteredCommand_shouldReturnTheHandlersClass() {
        // Given
        final Class commandClass = TestCommand.class;
        final Class registeredHandlerClass = TestCommandHandler.class;

        resolver.putCommandClass(registeredHandlerClass, Optional.<Class<? extends Command>>of(commandClass));

        // When
        final Optional<Class<? extends CommandHandler>> handlerClass = resolver.getHandlerClass(commandClass);

        // Then
        assertTrue(handlerClass.isPresent());
        assertSame(registeredHandlerClass, handlerClass.get());
    }

}
