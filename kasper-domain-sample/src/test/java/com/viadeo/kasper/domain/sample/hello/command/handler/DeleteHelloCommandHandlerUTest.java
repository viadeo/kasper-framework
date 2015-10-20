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
