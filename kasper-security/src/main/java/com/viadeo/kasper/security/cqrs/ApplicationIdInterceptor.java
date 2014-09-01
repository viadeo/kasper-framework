// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.cqrs;

import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.security.exception.KasperInvalidApplicationIdException;
import org.apache.commons.lang.StringUtils;

public class ApplicationIdInterceptor<C extends Object> implements Interceptor<C, Object>  {

    @Override
    public Object process(C c, Context context, InterceptorChain<C, Object> chain) throws Exception {

        if(StringUtils.isBlank(context.getApplicationId())){
            throw new KasperInvalidApplicationIdException("X-KASPER-CLIENT-APPID header must be filled",
                    CoreReasonCode.INVALID_INPUT);
        }

        return chain.next(c, context);
    }
}
