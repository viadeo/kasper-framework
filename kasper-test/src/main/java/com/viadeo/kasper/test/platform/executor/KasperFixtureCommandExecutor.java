// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform.executor;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.test.platform.validator.KasperFixtureCommandResultValidator;

public interface KasperFixtureCommandExecutor<VALIDATOR extends KasperFixtureCommandResultValidator> extends KasperFixtureExecutor {

    VALIDATOR when(Command command);

    VALIDATOR when(Command command, Context context);

}
