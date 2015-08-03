// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.viadeo.kasper.api.annotation.XKasperCommand;
import com.viadeo.kasper.api.annotation.XKasperDomain;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperResponse;
import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.core.resolvers.CommandHandlerResolver;
import com.viadeo.kasper.core.resolvers.CommandResolver;
import com.viadeo.kasper.core.resolvers.DomainResolver;
import com.viadeo.kasper.core.resolvers.ResolverFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MeasuredCommandHandlerUTest {

    @Mock
    private MetricRegistry metricRegistry;

    @Before
    public void setUp() throws Exception {
        when(metricRegistry.meter(anyString())).thenReturn(mock(Meter.class));
        Timer timer = mock(Timer.class);
        when(timer.time()).thenReturn(mock(Timer.Context.class));
        when(metricRegistry.timer(anyString())).thenReturn(timer);

        DomainResolver domainResolver = new DomainResolver();

        CommandResolver commandResolver = new CommandResolver();
        commandResolver.setDomainResolver(domainResolver);

        CommandHandlerResolver commandHandlerResolver = new CommandHandlerResolver();
        commandHandlerResolver.setDomainResolver(domainResolver);

        ResolverFactory resolverFactory = new ResolverFactory();
        resolverFactory.setCommandResolver(commandResolver);
        resolverFactory.setCommandHandlerResolver(commandHandlerResolver);

        KasperMetrics.setResolverFactory(resolverFactory);
    }

    @Test
    public void measure_an_accepted_command() throws Exception {
        // Given
        MeasuredCommandHandler handler = new MeasuredCommandHandler(
                metricRegistry,
                new TestCommandHandler(KasperResponse.Status.ACCEPTED)
        );

        // When
        CommandResponse response = handler.handle(new CommandMessage<Command>(Contexts.empty(), mock(TestCommand.class)));

        // Then
        assertNotNull(response);
        verify(metricRegistry).meter("unknown.command.testcommand.requests");
        verify(metricRegistry).meter("unknown.command.requests");
        verify(metricRegistry).meter("client.unknown.command.requests");
        verify(metricRegistry).timer("unknown.command.testcommand.requests-time");

    }

    @Test
    public void measure_an_ok_command() throws Exception {
        // Given
        MeasuredCommandHandler handler = new MeasuredCommandHandler(
                metricRegistry,
                new TestCommandHandler(KasperResponse.Status.OK)
        );

        // When
        CommandResponse response = handler.handle(new CommandMessage<Command>(Contexts.empty(), mock(TestCommand.class)));

        // Then
        assertNotNull(response);
        verify(metricRegistry).meter("unknown.command.testcommand.requests");
        verify(metricRegistry).meter("unknown.command.requests");
        verify(metricRegistry).meter("client.unknown.command.requests");
        verify(metricRegistry).timer("unknown.command.testcommand.requests-time");
    }

    @Test
    public void measure_a_refused_command() throws Exception {
        // Given
        MeasuredCommandHandler handler = new MeasuredCommandHandler(
                metricRegistry,
                new TestCommandHandler(KasperResponse.Status.REFUSED)
        );

        // When
        CommandResponse response = handler.handle(new CommandMessage<Command>(Contexts.empty(), mock(TestCommand.class)));

        // Then
        assertNotNull(response);
        verify(metricRegistry).meter("unknown.command.testcommand.requests");
        verify(metricRegistry).meter("unknown.command.requests");
        verify(metricRegistry).meter("client.unknown.command.requests");
        verify(metricRegistry).timer("unknown.command.testcommand.requests-time");
    }

    @Test
    public void measure_an_error_command() throws Exception {
        // Given
        MeasuredCommandHandler handler = new MeasuredCommandHandler(
                metricRegistry,
                new TestCommandHandler(KasperResponse.Status.ERROR)
        );

        // When
        CommandResponse response = handler.handle(new CommandMessage<Command>(Contexts.empty(), mock(TestCommand.class)));

        // Then
        assertNotNull(response);
        verify(metricRegistry).meter("unknown.command.testcommand.requests");
        verify(metricRegistry).meter("unknown.command.requests");
        verify(metricRegistry).meter("client.unknown.command.requests");
        verify(metricRegistry).timer("unknown.command.testcommand.requests-time");
        verify(metricRegistry).meter("unknown.command.testcommand.errors");
        verify(metricRegistry).meter("unknown.command.errors");
        verify(metricRegistry).meter("client.unknown.command.errors");
    }

    @Test
    public void measure_a_command_throwing_an_unexpected_exception() throws Exception {
        // Given
        MeasuredCommandHandler handler = new MeasuredCommandHandler(
                metricRegistry,
                new TestCommandHandler(KasperResponse.Status.ERROR) {
                    @Override
                    public CommandResponse handle(Context context, TestCommand command) {
                        throw new RuntimeException("Fake exception");
                    }
                }
        );

        // When
        CommandResponse response = handler.handle(new CommandMessage<Command>(Contexts.empty(), mock(TestCommand.class)));

        // Then
        assertNotNull(response);
        verify(metricRegistry).meter("unknown.command.testcommand.requests");
        verify(metricRegistry).meter("unknown.command.requests");
        verify(metricRegistry).meter("client.unknown.command.requests");
        verify(metricRegistry).timer("unknown.command.testcommand.requests-time");
        verify(metricRegistry).meter("unknown.command.testcommand.errors");
        verify(metricRegistry).meter("unknown.command.errors");
        verify(metricRegistry).meter("client.unknown.command.errors");
    }

    @XKasperUnregistered
    @XKasperDomain(prefix = "test", label = "test")
    public static class TestDomain implements Domain { }

    @XKasperUnregistered
    @XKasperCommand()
    private static class TestCommand implements Command {}

    @XKasperUnregistered
    @XKasperCommandHandler(domain = TestDomain.class)
    private static class TestCommandHandler extends AutowiredCommandHandler<TestCommand> {

        private final KasperResponse.Status status;

        public TestCommandHandler(KasperResponse.Status status) {
            this.status = status;
        }

        @Override
        public CommandResponse handle(Context context, TestCommand command) {
            switch (status) {
                case OK:
                    return CommandResponse.ok();
                case ACCEPTED:
                    return CommandResponse.accepted();
                case REFUSED:
                    return CommandResponse.refused(CoreReasonCode.UNKNOWN_REASON);
                case ERROR:
                    return CommandResponse.error(CoreReasonCode.UNKNOWN_REASON);
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }
}
