// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

import java.util.List;

import static com.google.common.base.Preconditions.*;

public class CompositeInterceptorFactory<INPUT, OUTPUT> implements InterceptorFactory<INPUT, OUTPUT> {

    private final List<InterceptorFactory<INPUT, OUTPUT>> factories;

    public CompositeInterceptorFactory(final List<InterceptorFactory<INPUT, OUTPUT>> factories) {
        this.factories = checkNotNull(factories);
    }

    @Override
    public Optional<InterceptorChain<INPUT, OUTPUT>> create(final TypeToken<?> type) {
        return create(type, null);
    }

    public Optional<InterceptorChain<INPUT, OUTPUT>> create(final TypeToken<?> type,
                                                            final InterceptorChain<INPUT, OUTPUT> givenTail) {
        checkNotNull(type);

        if (!accept(type)) {
            return Optional.absent();
        }

        InterceptorChain<INPUT, OUTPUT> tail = givenTail;

        for (final InterceptorFactory<INPUT, OUTPUT> factory : Lists.reverse(factories)) {
            Optional<InterceptorChain<INPUT, OUTPUT>> optChain = factory.create(type);

            if (optChain.isPresent()) {
                if (null == tail) {
                    tail = optChain.get();
                } else {
                    tail = optChain.get().withNextChain(tail);
                }
            }
        }

        return Optional.fromNullable(tail);
    }

    @Override
    public boolean accept(TypeToken<?> type) {
        return factories.size() > 0;
    }
}
