package com.viadeo.kasper.cqrs.command.impl;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.context.impl.DefaultContext;
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

public class KasperCommandGatewayITest {

    private KasperCommandGateway commandGateway;

    @Before
    public void setUp() throws Exception {
        final UnitOfWorkFactory uowFactory = new DefaultUnitOfWorkFactory();
        final KasperCommandBus commandBus = new KasperCommandBus();
        commandBus.setUnitOfWorkFactory(uowFactory);

        this.commandGateway = new KasperCommandGateway(commandBus);

        KasperMetrics.setMetricRegistry(new MetricRegistry());
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
        Command command = new TestCommand();

        // When
        commandGateway.sendCommand(command, new DefaultContext());

        // Then forget the call
    }

    public static class TestCommand implements Command {

    }
}
