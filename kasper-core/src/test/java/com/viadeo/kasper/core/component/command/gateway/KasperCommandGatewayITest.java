package com.viadeo.kasper.core.component.command.gateway;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.context.Tags;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;
import com.viadeo.kasper.core.component.command.KasperCommandBus;
import com.viadeo.kasper.core.component.command.CommandMessage;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.axonframework.unitofwork.DefaultUnitOfWorkFactory;
import org.axonframework.unitofwork.UnitOfWorkFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class KasperCommandGatewayITest {

    static {
        // pre-load Tags class, to initialize its fields
        try {
            Class.forName(Tags.class.getName());
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private KasperCommandGateway commandGateway;

    // ------------------------------------------------------------------------

    public static class TestCommand implements Command {
    }

    // ------------------------------------------------------------------------

    @Before
    public void setUp() throws Exception {
        final UnitOfWorkFactory uowFactory = new DefaultUnitOfWorkFactory();
        final KasperCommandBus commandBus = new KasperCommandBus();
        commandBus.setUnitOfWorkFactory(uowFactory);

        final MetricRegistry metricRegistry = new MetricRegistry();

        this.commandGateway = new KasperCommandGateway(commandBus, metricRegistry);

        KasperMetrics.setMetricRegistry(metricRegistry);
    }

    // ------------------------------------------------------------------------

    final Object lock = new Object();

    @Test
    public void sendCommand_isOk() throws Exception {
        // Given
        final List<Command> captor = Lists.newArrayList();

        commandGateway.register(new AutowiredCommandHandler<TestCommand>() {
            @Override
            public CommandResponse handle(final CommandMessage message) throws Exception {
                captor.add(message.getCommand());
                synchronized (lock) {
                    lock.notify();
                }
                return CommandResponse.ok();
            }
        });

        final Command command = new TestCommand();

        // When
        commandGateway.sendCommand(command, Contexts.empty());

        synchronized (lock) {
            lock.wait(500);
        }

        // Then
        assertEquals(1, captor.size());
        assertEquals(command, captor.get(0));
    }

    @Test(timeout = 100)
    public void sendCommand_shouldFireAndForget() throws Exception {
        // Given
        commandGateway.register(new AutowiredCommandHandler<TestCommand>() {
            @Override
            public CommandResponse handle(CommandMessage message) throws Exception {
                Thread.sleep(5000);
                return CommandResponse.ok();
            }
        });
        final Command command = new TestCommand();

        // When
        commandGateway.sendCommand(command, Contexts.empty());

        // Then forget the call
    }

}
