// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform.fixture;

import com.viadeo.kasper.test.platform.executor.KasperFixtureEventExecutor;
import com.viadeo.kasper.test.platform.validator.KasperFixtureEventResultValidator;

public interface KasperEventFixture <EXECUTOR extends KasperFixtureEventExecutor<VALIDATOR>, VALIDATOR extends KasperFixtureEventResultValidator>
        extends KasperFixture<EXECUTOR>  {
    
}
