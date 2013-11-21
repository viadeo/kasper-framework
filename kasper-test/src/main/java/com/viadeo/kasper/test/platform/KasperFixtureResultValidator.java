// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import org.hamcrest.Matcher;

public interface KasperFixtureResultValidator {

    KasperFixtureCommandResultValidator expectException(Class<? extends Throwable> expectedException);

    KasperFixtureCommandResultValidator expectException(Matcher<?> matcher);

}
