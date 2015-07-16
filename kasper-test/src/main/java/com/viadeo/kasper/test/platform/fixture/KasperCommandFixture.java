// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform.fixture;

import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.test.platform.executor.KasperFixtureCommandExecutor;
import com.viadeo.kasper.test.platform.validator.KasperFixtureCommandResultValidator;

import java.util.List;

public interface KasperCommandFixture<EXECUTOR extends KasperFixtureCommandExecutor<VALIDATOR>, VALIDATOR extends KasperFixtureCommandResultValidator>
        extends KasperFixture<EXECUTOR> {

    EXECUTOR givenCommands(final Command... commands);

    EXECUTOR givenCommands(final List<Command> commands);

    EXECUTOR givenCommands(final Context context, final Command... commands);

    EXECUTOR givenCommands(final Context context, final List<Command> commands);

}
