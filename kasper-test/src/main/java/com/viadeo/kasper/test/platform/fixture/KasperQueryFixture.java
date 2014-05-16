// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform.fixture;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.event.IEvent;
import com.viadeo.kasper.test.platform.executor.KasperFixtureQueryExecutor;
import com.viadeo.kasper.test.platform.validator.KasperFixtureQueryResultValidator;

import java.util.List;

public interface KasperQueryFixture<EXECUTOR extends KasperFixtureQueryExecutor, VALIDATOR extends KasperFixtureQueryResultValidator>
    extends KasperFixture<EXECUTOR> {

    EXECUTOR givenEvents(final IEvent... events);

    EXECUTOR givenEvents(final List<IEvent> events);

    EXECUTOR givenEvents(final Context context, final IEvent... events);

    EXECUTOR givenEvents(final Context context, final List<IEvent> events);

}
