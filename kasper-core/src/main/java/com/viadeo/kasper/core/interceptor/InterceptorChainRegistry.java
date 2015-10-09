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
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class InterceptorChainRegistry<INPUT, OUTPUT> {

    private final List<InterceptorFactory<INPUT, OUTPUT>> interceptorFactories;
    private final ConcurrentMap<Class, InterceptorChain<INPUT, OUTPUT>> chains;

    // ------------------------------------------------------------------------

    public InterceptorChainRegistry() {
        this(Lists.<InterceptorFactory<INPUT, OUTPUT>>newArrayList());
    }

    public InterceptorChainRegistry(final List<InterceptorFactory<INPUT, OUTPUT>> interceptorFactories) {
        this.interceptorFactories = Lists.newArrayList(checkNotNull(interceptorFactories));
        this.chains = Maps.newConcurrentMap();
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Optional<InterceptorChain<INPUT, OUTPUT>> create(final Class key,
                                                            final InterceptorFactory<INPUT, OUTPUT> tailFactory) {
        checkNotNull(key);
        checkNotNull(tailFactory);

        Optional<InterceptorChain<INPUT, OUTPUT>> optionalInterceptorChain = get(key);

        if( ! optionalInterceptorChain.isPresent()){
            final List<InterceptorFactory<INPUT, OUTPUT>> factories = Lists.newArrayList();
            factories.addAll(interceptorFactories);
            factories.add(tailFactory);

            optionalInterceptorChain = new CompositeInterceptorFactory<>(factories).create(TypeToken.of(key));
            chains.putIfAbsent(key, optionalInterceptorChain.get());
        }

        return optionalInterceptorChain;
    }

    // ------------------------------------------------------------------------

    public Optional<InterceptorChain<INPUT, OUTPUT>> get(final Class key) {
        return Optional.fromNullable(chains.get(checkNotNull(key)));
    }

    // ------------------------------------------------------------------------

    public void register(InterceptorFactory<INPUT, OUTPUT> interceptorFactory) {
        this.interceptorFactories.add(checkNotNull(interceptorFactory));
    }

}
