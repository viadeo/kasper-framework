package com.viadeo.kasper.cqrs.command.impl;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.viadeo.kasper.context.Contexts;
import com.viadeo.kasper.context.Tags;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import org.axonframework.unitofwork.DefaultUnitOfWorkFactory;
import org.axonframework.unitofwork.UnitOfWork;
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

        this.commandGateway = new KasperCommandGateway(commandBus);

        KasperMetrics.setMetricRegistry(new MetricRegistry());
    }

    // ------------------------------------------------------------------------

    final Object lock = new Object();

    @Test
    public void sendCommand_isOk() throws Exception {
        // Given
        final List<Command> captor = Lists.newArrayList();

        commandGateway.register(new CommandHandler<TestCommand>() {
            @Override
            public CommandResponse handle(final KasperCommandMessage message, final UnitOfWork uow) throws Exception {
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
        commandGateway.register(new CommandHandler<TestCommand>() {
            @Override
            public CommandResponse handle(KasperCommandMessage message, UnitOfWork uow) throws Exception {
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
