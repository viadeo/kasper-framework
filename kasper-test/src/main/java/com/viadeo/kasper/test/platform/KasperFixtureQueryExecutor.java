// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.query.Query;

public interface KasperFixtureQueryExecutor<VALIDATOR extends KasperFixtureQueryResultValidator> extends KasperFixtureExecutor {

    VALIDATOR when(Query query);

    VALIDATOR when(Query query, Context context);

}
