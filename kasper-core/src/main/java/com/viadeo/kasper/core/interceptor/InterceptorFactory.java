// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;

public interface InterceptorFactory<INPUT, OUTPUT> {

    Optional<InterceptorChain<INPUT, OUTPUT>> create(TypeToken<?> type);

    boolean accept(TypeToken<?> type);

}
