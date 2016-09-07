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
