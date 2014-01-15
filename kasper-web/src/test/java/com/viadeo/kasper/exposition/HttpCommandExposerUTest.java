package com.viadeo.kasper.exposition;

import com.google.common.collect.Lists;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import org.junit.Test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpCommandExposerUTest {

    public static class ACommand implements Command {
        private static final long serialVersionUID = -4789744274328803972L;
    }

    public static class CommandHandlerA extends CommandHandler<ACommand> { }

    public static class CommandHandlerB extends CommandHandler<ACommand> { }

    @Test(expected = HttpExposerError.class)
    public void init_withTwoEventListeners_listeningTheSameEvent_isOk() throws Exception {
        // Given
        final List<ExposureDescriptor<Command, CommandHandler>> descriptors = Lists.newArrayList(
                new ExposureDescriptor<Command, CommandHandler>(ACommand.class, CommandHandlerA.class),
                new ExposureDescriptor<Command, CommandHandler>(ACommand.class, CommandHandlerB.class)
        );

        ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getContextPath()).thenReturn("");

        ServletConfig servletConfig = mock(ServletConfig.class);
        when(servletConfig.getServletContext()).thenReturn(servletContext);

        HttpCommandExposer commandExposer = new HttpCommandExposer(mock(CommandGateway.class), descriptors);

        // When
        commandExposer.init(servletConfig);

        // Then throw an exception
    }
}
