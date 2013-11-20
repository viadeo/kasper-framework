// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.command.Command;

public interface KasperFixtureCommandExecutor<VALIDATOR> extends KasperFixtureExecutor<VALIDATOR> {

    VALIDATOR when(Command command);

    VALIDATOR when(Command command, Context context);

}
