// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor;

import com.viadeo.kasper.context.Context;

public interface Interceptor<INPUT, OUTPUT> {

    OUTPUT process(final INPUT input,
                   final Context context,
                   final InterceptorChain<INPUT, OUTPUT> chain) throws Exception;

}
