// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform.validator.base;

import org.hamcrest.Matcher;

public interface ExceptionValidator<VALIDATOR extends ExceptionValidator> {

    VALIDATOR expectException(Class<? extends Throwable> expectedException);

    VALIDATOR expectException(Matcher<?> matcher);

}
