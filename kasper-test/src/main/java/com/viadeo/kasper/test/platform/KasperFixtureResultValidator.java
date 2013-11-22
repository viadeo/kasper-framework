// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import org.hamcrest.Matcher;

public interface KasperFixtureResultValidator {

    KasperFixtureResultValidator expectReturnOK();

    KasperFixtureResultValidator expectReturnError(KasperReason reason);

    KasperFixtureResultValidator expectReturnRefused(KasperReason reason);

    KasperFixtureResultValidator expectException(Class<? extends Throwable> expectedException);

    KasperFixtureResultValidator expectException(Matcher<?> matcher);

    KasperFixtureResultValidator expectValidationErrorOnField(String field);

}
