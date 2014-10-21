// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.interceptor;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import org.slf4j.MDC;

public class MDCInterceptor<INPUT, OUTPUT>  implements Interceptor<INPUT, OUTPUT> {

    @Override
    public OUTPUT process(final INPUT input, final Context context, final InterceptorChain<INPUT, OUTPUT> chain) throws Exception {
        MDC.setContextMap(context.asMap());
        return chain.next(input, context);
    }

}
