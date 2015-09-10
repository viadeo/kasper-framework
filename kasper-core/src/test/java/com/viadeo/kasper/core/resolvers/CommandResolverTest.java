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
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.locators.DomainLocator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class CommandResolverTest {

    @XKasperUnregistered
    private static class TestDomain implements Domain {}

    @XKasperUnregistered
    private static class TestCommand implements Command { }

    // ------------------------------------------------------------------------

    @Test
    public void testGetDomain() {
        // Given
        final CommandResolver resolver = new CommandResolver();
        final DomainLocator domainLocator = mock(DomainLocator.class);
        final CommandHandlerResolver commandHandlerResolver = mock(CommandHandlerResolver.class);
        final CommandHandler commandHandler = mock(CommandHandler.class);
        final DomainResolver domainResolver = mock(DomainResolver.class);

        resolver.setDomainLocator(domainLocator);
        resolver.setCommandHandlerResolver(commandHandlerResolver);
        resolver.setDomainResolver(domainResolver);

        when(domainLocator.getHandlerForCommandClass(TestCommand.class))
                .thenReturn( Optional.<CommandHandler>of(commandHandler) );

        when( commandHandlerResolver.getDomainClass(commandHandler.getClass()) )
                .thenReturn( Optional.<Class<? extends Domain>>of(TestDomain.class) );

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomainClass(TestCommand.class);

        // Then
        assertTrue(domain.isPresent());
        assertEquals(TestDomain.class, domain.get());

        verify(domainLocator, times(1)).getHandlerForCommandClass(TestCommand.class);
        verifyNoMoreInteractions(domainLocator);

        verify(commandHandlerResolver, times(1)).getDomainClass(commandHandler.getClass());
        verifyNoMoreInteractions(commandHandlerResolver);
    }

}
