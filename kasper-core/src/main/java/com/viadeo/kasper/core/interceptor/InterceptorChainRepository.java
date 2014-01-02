// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class InterceptorChainRepository<INPUT, OUTPUT> {

    private final List<InterceptorFactory<INPUT, OUTPUT>> interceptorFactories;
    private final Map<Class, InterceptorChain<INPUT, OUTPUT>> chains;

    public InterceptorChainRepository() {
        this.interceptorFactories = Lists.newArrayList();
        this.chains = Maps.newHashMap();
    }

    public void register(final InterceptorFactory<INPUT, OUTPUT> interceptorFactory) {
        this.interceptorFactories.add(checkNotNull(interceptorFactory));
    }

    public Optional<InterceptorChain<INPUT, OUTPUT>> create(final Class key,
                                                            final Interceptor<INPUT, OUTPUT> tail) {
        checkNotNull(key);
        checkNotNull(tail);

        Optional<InterceptorChain<INPUT, OUTPUT>> optionalChain = new CompositeInterceptorFactory<>(
                interceptorFactories
        ).create(TypeToken.of(key), InterceptorChain.makeChain(tail));

        final InterceptorChain<INPUT, OUTPUT> interceptorChain;

        if (optionalChain.isPresent()) {
            interceptorChain = optionalChain.get();
        } else {
            interceptorChain = InterceptorChain.makeChain(tail);
        }

        chains.put(key, interceptorChain);

        return Optional.of(interceptorChain);
    }

    public Optional<InterceptorChain<INPUT, OUTPUT>> get(final Class key) {
        return Optional.fromNullable(chains.get(checkNotNull(key)));
    }

}
