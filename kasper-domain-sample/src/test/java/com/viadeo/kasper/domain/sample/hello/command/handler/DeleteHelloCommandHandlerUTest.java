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
package com.viadeo.kasper.domain.sample.hello.command.handler;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.id.DefaultKasperId;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.domain.sample.hello.api.command.DeleteHelloCommand;
import com.viadeo.kasper.domain.sample.hello.command.entity.Hello;
import com.viadeo.kasper.domain.sample.hello.command.repository.HelloRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @see DeleteHelloCommandHandler
 */
@Ignore
//@RunWith(MockitoJUnitRunner.class)
public class DeleteHelloCommandHandlerUTest {

    private static final KasperID HELLO_ID = DefaultKasperId.random();

    @InjectMocks
    private DeleteHelloCommandHandler handler;

    /*
     * Expectations are done on a different object from the object used in the code :
     *  => In the code we use the method "Optional<AGR> ClientRepository.load", in the test we use the method "Hello HelloRepository.load".
     */
//    @Mock
    public HelloRepository repository;

    @Before
    public void setUp() throws Exception {
        repository = spy(new HelloRepository());
    }

    @Test
    public void handle_withNotFoundHello_shouldReturnNotFoundError() throws Exception {
        // Given
        DeleteHelloCommand command = new DeleteHelloCommand(HELLO_ID);
        when(repository.load(HELLO_ID)).thenReturn(Optional.<Hello>absent());

        // When
        CommandResponse commandResponse = handler.handle(command);

        // Then
        assertFalse(commandResponse.isOK());
        assertNotNull(commandResponse.getReason());
        assertEquals(CoreReasonCode.NOT_FOUND.name(), commandResponse.getReason().getCode());
    }

    @Test
    public void handle_withExistingHello_shouldDeleteHello() throws Exception {
        // Given
        DeleteHelloCommand command = new DeleteHelloCommand(HELLO_ID);
        Hello hello = mock(Hello.class);
        when(repository.load(any(KasperID.class), any(Long.class))).thenReturn(Optional.of(hello));

        // When
        CommandResponse commandResponse = handler.handle(command);

        // Then
        assertTrue(commandResponse.isOK());
        verify(hello).delete();
    }
}
