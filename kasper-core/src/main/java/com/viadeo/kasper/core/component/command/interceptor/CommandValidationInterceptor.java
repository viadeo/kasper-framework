// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.interceptor;

import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.interceptor.BaseValidationInterceptor;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;

import javax.validation.ValidatorFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public class CommandValidationInterceptor<C extends Command>
        extends BaseValidationInterceptor<C>
        implements Interceptor<C, CommandResponse> {

    public CommandValidationInterceptor(final ValidatorFactory validatorFactory) {
        super(checkNotNull(validatorFactory));
    }

    // ------------------------------------------------------------------------

    @Override
    public CommandResponse process(
            final C c,
            final Context context,
            final InterceptorChain<C, CommandResponse> chain) throws Exception {
        validate(c);
        return chain.next(c, context);
    }

}
