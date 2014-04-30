// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform.executor;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.test.platform.validator.KasperFixtureEventResultValidator;

public interface KasperFixtureEventExecutor<VALIDATOR extends KasperFixtureEventResultValidator> extends KasperFixtureExecutor {

    VALIDATOR when(Event event);

    VALIDATOR when(Event event, Context context);

}
