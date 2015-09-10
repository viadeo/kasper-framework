// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommandHandlerResolverTest {

    @XKasperUnregistered
    private static class TestDomain implements Domain {}

    @XKasperUnregistered
    @XKasperCommandHandler( domain = TestDomain.class )
    private static class TestCommandHandler extends AutowiredCommandHandler { }

    @XKasperUnregistered
    private static class TestCommand implements Command { }

    @XKasperUnregistered
    private static class TestCommandHandler2 extends AutowiredCommandHandler<TestCommand> { }

    // ------------------------------------------------------------------------

    @Test
    public void testGetDomainFromCommandHandler() {
        // Given
        final CommandHandlerResolver resolver = new CommandHandlerResolver();

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomainClass(TestCommandHandler.class);

        // Then
        assertTrue(domain.isPresent());
        assertEquals(TestDomain.class, domain.get());
    }

    @Test
    public void testGetCommandFromValidHandler() {
        // Given
        final CommandHandlerResolver resolver = new CommandHandlerResolver();

        // When
        final Class<? extends Command> command =
                resolver.getCommandClass(TestCommandHandler2.class);

        // Then
        assertEquals(TestCommand.class, command);
    }

    @Test
    public void testGetCommandFromInvalidHandler() {
        // Given
        final CommandHandlerResolver resolver = new CommandHandlerResolver();

        // When
        try {
            resolver.getCommandClass(TestCommandHandler.class);
            fail();
        } catch (final KasperException e) {
            // Then exception is raised
        }
    }

}
