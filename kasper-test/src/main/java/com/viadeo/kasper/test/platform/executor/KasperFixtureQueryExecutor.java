// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform.executor;

import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.test.platform.validator.KasperFixtureQueryResultValidator;

public interface KasperFixtureQueryExecutor<VALIDATOR extends KasperFixtureQueryResultValidator> extends KasperFixtureExecutor {

    VALIDATOR when(Query query);

    VALIDATOR when(Query query, Context context);

}
