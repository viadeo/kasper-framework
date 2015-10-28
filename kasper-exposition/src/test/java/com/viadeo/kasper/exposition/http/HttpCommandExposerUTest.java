// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.core.component.annotation.XKasperUnexposed;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.exposition.ExposureDescriptor;
import com.viadeo.kasper.platform.Meta;
import com.viadeo.kasper.platform.Platform;
import org.junit.Test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpCommandExposerUTest {

    public static class ACommand implements Command {
        private static final long serialVersionUID = -4289744274328803942L;
    }

    public static class CommandHandlerA extends AutowiredCommandHandler<ACommand> { }

    public static class CommandHandlerB extends AutowiredCommandHandler<ACommand> { }

    @XKasperUnexposed
    public static class CommandHandlerC extends AutowiredCommandHandler<ACommand> { }

    @Test(expected = HttpExposerError.class)
    public void init_withTwoHandlers_handlingTheSameCommand_throwException() throws Exception {
        // Given
        @SuppressWarnings("unchecked")
        final List<ExposureDescriptor<Command, CommandHandler>> descriptors = Lists.newArrayList(
                new ExposureDescriptor<Command, CommandHandler>(ACommand.class, CommandHandlerA.class),
                new ExposureDescriptor<Command, CommandHandler>(ACommand.class, CommandHandlerB.class)
        );

        final ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getContextPath()).thenReturn("");

        final ServletConfig servletConfig = mock(ServletConfig.class);
        when(servletConfig.getServletContext()).thenReturn(servletContext);

        final Platform platform = mock(Platform.class);
        when(platform.getCommandGateway()).thenReturn(mock(CommandGateway.class));
        when(platform.getMeta()).thenReturn(mock(Meta.class));

        final HttpCommandExposer commandExposer = new HttpCommandExposer(platform, descriptors);

        // When
        commandExposer.init(servletConfig);

        // Then throw an exception
    }

    @Test
    public void isExposable_withUnexposedAnnotation_returnFalse() {
        // Given
        final Platform platform = mock(Platform.class);
        when(platform.getCommandGateway()).thenReturn(mock(CommandGateway.class));
        when(platform.getMeta()).thenReturn(mock(Meta.class));

        final HttpCommandExposer exposer = new HttpCommandExposer(
                platform,
                Lists.<ExposureDescriptor<Command, CommandHandler>>newArrayList()
        );

        // When
        boolean exposable = exposer.isExposable(
                new ExposureDescriptor<Command, CommandHandler>(
                        ACommand.class,
                        CommandHandlerC.class
                )
        );

        // Then
        assertFalse(exposable);
    }

    @Test
    public void isExposable_withoutUnexposedAnnotation_returnTrue() {
        // Given
        final Platform platform = mock(Platform.class);
        when(platform.getCommandGateway()).thenReturn(mock(CommandGateway.class));
        when(platform.getMeta()).thenReturn(mock(Meta.class));

        final HttpCommandExposer exposer = new HttpCommandExposer(
                platform,
                Lists.<ExposureDescriptor<Command, CommandHandler>>newArrayList()
        );

        // When
        boolean exposable = exposer.isExposable(
                new ExposureDescriptor<Command, CommandHandler>(
                        ACommand.class,
                        CommandHandlerB.class
                )
        );

        // Then
        assertTrue(exposable);
    }
}
