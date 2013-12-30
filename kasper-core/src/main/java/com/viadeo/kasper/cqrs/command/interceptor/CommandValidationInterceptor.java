// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.interceptor;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.BaseValidationInterceptor;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.cqrs.command.Command;

import javax.validation.ValidatorFactory;

public class CommandValidationInterceptor<C extends Command>
        extends BaseValidationInterceptor<C>
        implements Interceptor<C, C> {

    public CommandValidationInterceptor(ValidatorFactory validatorFactory) {
        super(validatorFactory);
    }

    @Override
    public C process(C c, Context context, InterceptorChain<C, C> chain) throws Exception {
        validate(c);
        return chain.next(c, context);
    }

}
