package com.viadeo.kasper.cqrs.interceptor;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import org.slf4j.MDC;

public class MDCInterceptor<INPUT, OUTPUT>  implements Interceptor<INPUT, OUTPUT> {
    @Override
    public OUTPUT process(INPUT input, Context context, InterceptorChain<INPUT, OUTPUT> chain) throws Exception {
        MDC.setContextMap(context.asMap());
        return chain.next(input, context);
    }
}
