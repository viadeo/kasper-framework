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
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;
import com.viadeo.kasper.core.component.command.CommandHandler;
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
    private static class TestCommandHandler extends AutowiredCommandHandler<TestCommand> {
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
