// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.eventhandling.EventBus;

import java.util.List;

public interface KasperFixture<
        EXECUTOR extends KasperFixtureExecutor,
        VALIDATOR extends KasperFixtureResultValidator> {

    KasperFixture<EXECUTOR, VALIDATOR> registerCommandHandler(final CommandHandler commandHandler);

    // ------------------------------------------------------------------------

    KasperFixtureExecutor<VALIDATOR> given();

    KasperFixtureExecutor<VALIDATOR> givenCommands(final Command... commands);

    KasperFixtureExecutor<VALIDATOR> givenCommands(final List<Command> commands);

    // ------------------------------------------------------------------------

    public CommandBus commandBus();

    public EventBus eventBus();

}
