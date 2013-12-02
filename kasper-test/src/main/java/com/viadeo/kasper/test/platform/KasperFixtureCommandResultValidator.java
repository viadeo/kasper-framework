// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.event.IEvent;

public interface KasperFixtureCommandResultValidator extends KasperFixtureResultValidator {

    KasperFixtureCommandResultValidator expectSequenceOfEvents(IEvent... events);

    KasperFixtureCommandResultValidator expectExactSequenceOfEvents(IEvent... events);

    KasperFixtureCommandResultValidator expectReturnResponse(CommandResponse queryResponse);

}
