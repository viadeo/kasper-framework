// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform.validator;

import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.test.platform.validator.base.ReturnTypeValidator;

public interface KasperFixtureCommandResultValidator extends ReturnTypeValidator<KasperFixtureCommandResultValidator> {

    KasperFixtureCommandResultValidator expectSequenceOfEvents(Event... events);

    KasperFixtureCommandResultValidator expectExactSequenceOfEvents(Event... events);

    KasperFixtureCommandResultValidator expectReturnResponse(CommandResponse queryResponse);

}
