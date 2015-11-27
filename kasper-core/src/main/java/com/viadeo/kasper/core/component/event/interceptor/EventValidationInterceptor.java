// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.interceptor;

import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.cache.BaseValidationInterceptor;

import javax.validation.ValidatorFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public class EventValidationInterceptor<C extends Event>
        extends BaseValidationInterceptor<C>
        implements Interceptor<C, Void> {

    public EventValidationInterceptor(final ValidatorFactory validatorFactory) {
        super(checkNotNull(validatorFactory));
    }

    // ------------------------------------------------------------------------

    @Override
    public Void process(
            final C c,
            final Context context,
            final InterceptorChain<C, Void> chain) {
        validate(c);
        return chain.next(c, context);
    }

}
