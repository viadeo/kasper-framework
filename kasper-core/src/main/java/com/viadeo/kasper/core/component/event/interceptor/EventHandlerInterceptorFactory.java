// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.interceptor;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.api.component.event.Event;

public abstract class EventHandlerInterceptorFactory extends EventInterceptorFactory {

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<InterceptorChain<Event, Void>> create(final TypeToken<?> type) {
        final Interceptor<Event, Void> interceptor;

        try {
            interceptor = getEventHandlerInterceptor();
        } catch (final Exception e) {
            return Optional.absent();
        }

        return Optional.of(InterceptorChain.makeChain(interceptor));
    }

    protected abstract Interceptor<Event, Void> getEventHandlerInterceptor();

}
