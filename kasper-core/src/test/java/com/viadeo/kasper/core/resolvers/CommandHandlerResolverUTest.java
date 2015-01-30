package com.viadeo.kasper.core.resolvers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;

public class CommandHandlerResolverUTest {

    CommandHandlerResolver resolver;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
        Class<? extends Command> command = null;

        // Expect
        thrown.expect(NullPointerException.class);

        // When
        resolver.getHandlerClass(command);
    }

    @Test
    @SuppressWarnings("all")
    public void getHandlerClass_withUnregisteredCommand_shouldReturnNull() {
        // Given
        Class<? extends Command> command = TestCommand.class;

        // When
        Optional<Class<? extends CommandHandler>> handlerClass = resolver.getHandlerClass(command);

        // Then
        assertFalse(handlerClass.isPresent());
    }

    @Test
    public void getHandlerClass_withRegisteredCommand_shouldReturnTheHandlersClass() {
        // Given
        Class<? extends Command> command = TestCommand.class;
        Class<? extends CommandHandler> registeredHandler = TestCommandHandler.class;

        resolver.getCommandClass(registeredHandler);

        // When
        Optional<Class<? extends CommandHandler>> handlerClass = resolver.getHandlerClass(command);

        // Then
        assertTrue(handlerClass.isPresent());
        assertSame(registeredHandler, handlerClass.get());
    }

    // ------------------------------------------------------------------------

    @XKasperUnregistered
    private static class TestCommand implements Command {
    }

    @XKasperUnregistered
    private static class TestCommandHandler extends CommandHandler<TestCommand> {
    }

}
