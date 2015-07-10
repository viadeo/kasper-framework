// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.api.domain.response.CoreReasonCode;
import com.viadeo.kasper.api.domain.command.Command;
import com.viadeo.kasper.api.domain.command.CommandResponse;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.domain.MetaData;
import org.axonframework.unitofwork.CurrentUnitOfWork;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class CommandHandlerUTest {

    private enum ResponseType {
        OK, ACCEPT, REFUSE, ERROR, EXCEPTION
    }

    private static class TestCommandHandler extends CommandHandler<Command> {

        private final ResponseType responseType;

        public TestCommandHandler(final ResponseType responseType) {

            this.responseType = responseType;
        }

        @Override
        public CommandResponse handle(Command command) throws Exception {
            switch (responseType) {
                case OK:
                    return CommandResponse.ok();
                case ACCEPT:
                    return CommandResponse.accepted();
                case REFUSE:
                    return CommandResponse.refused(CoreReasonCode.UNKNOWN_REASON, "");
                case ERROR:
                    return CommandResponse.error(CoreReasonCode.UNKNOWN_REASON, "");
                case EXCEPTION:
                    throw new RuntimeException("expected exception for test!");
            }
            return null;
        }
    }

    private KasperUnitOfWork uow;

    @Before
    public void init() {
        KasperMetrics.setMetricRegistry(new MetricRegistry());
        uow = spy(KasperUnitOfWork.startAndGet());
        CurrentUnitOfWork.set(uow);
    }

    @Test
    public void handle_withOkResponse_shouldCommit() throws Throwable {
        // Given
        final TestCommandHandler handler = new TestCommandHandler(ResponseType.OK);

        // When
        final Object response = handler.handle(createCommandMessage(), uow);

        // Then
        assertNotNull(response);
        verifyNoMoreInteractions(uow);
    }

    @Test
    public void handle_withAcceptedResponse_shouldCommit() throws Throwable {
        // Given
        final TestCommandHandler handler = new TestCommandHandler(ResponseType.ACCEPT);

        // When
        final Object response = handler.handle(createCommandMessage(), uow);

        // Then
        assertNotNull(response);
        verifyNoMoreInteractions(uow);
    }

    @Test
    public void handle_withRefusedResponse_shouldRollback() throws Throwable {
        // Given
        final TestCommandHandler handler = new TestCommandHandler(ResponseType.REFUSE);

        // When
        final Object response = handler.handle(createCommandMessage(), uow);

        // Then
        assertNotNull(response);
        verify(uow).rollback();
    }

    @Test
    public void handle_withErroredResponse_shouldRollback() throws Throwable {
        // Given
        final TestCommandHandler handler = new TestCommandHandler(ResponseType.ERROR);

        // When
        final Object response = handler.handle(createCommandMessage(), uow);

        // Then
        assertNotNull(response);
        verify(uow).rollback();

    }

    @Test
    public void handle_withUnexpectedError_shouldRollback() throws Throwable {
        // Given
        final TestCommandHandler handler = new TestCommandHandler(ResponseType.EXCEPTION);

        // When
        try {
            handler.handle(createCommandMessage(), uow);
        } catch (RuntimeException e) {
            // nothing
        }

        // Then
        verify(uow).rollback(any(Throwable.class));
    }

    @SuppressWarnings("unchecked")
    private CommandMessage<Command> createCommandMessage() {
        final CommandMessage message = mock(CommandMessage.class);
        when(message.getMetaData()).thenReturn(MetaData.emptyInstance());
        return message;
    }
}
