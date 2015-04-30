// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.interceptor;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorFactory;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.context.MDCUtils;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class TagsInterceptor<C> implements Interceptor<C, Object> {

    @VisibleForTesting
    protected static final Map<Class<?>, Set<String>> CACHE_TAGS = Maps.newHashMap();

    private final Class<?> target;

    public TagsInterceptor(final TypeToken type) {
        checkNotNull(type);
        target = type.getRawType();
    }

    @Override
    public Object process(C c, Context context, InterceptorChain<C, Object> chain) throws Exception {
        checkNotNull(context);

        final Set<String> additionalTags = retrieveTags(target);
        final Context newContext = new Context.Builder(context)
                .addTags(additionalTags)
                .build();

        MDCUtils.enrichMdcContextMap(newContext);

        return chain.next(c, newContext);
    }

    @VisibleForTesting
    protected Set<String> retrieveTags(final Class<?> handlerClass) {
        checkNotNull(handlerClass);

        final Set<String> tags;

        if ( ! CACHE_TAGS.containsKey(handlerClass)) {
            tags = Sets.newHashSet();

            final XKasperQueryHandler queryAnnotation = handlerClass.getAnnotation(XKasperQueryHandler.class);
            if (null != queryAnnotation) {
                tags.addAll(ImmutableSet.copyOf(queryAnnotation.tags()));
            }

            final XKasperCommandHandler commandAnnotation = handlerClass.getAnnotation(XKasperCommandHandler.class);
            if (null != commandAnnotation) {
                tags.addAll(ImmutableSet.copyOf(commandAnnotation.tags()));
            }

            CACHE_TAGS.put(handlerClass, tags);

        } else {
            tags = CACHE_TAGS.get(handlerClass);
        }

        return tags;
    }

    public static class Factory implements InterceptorFactory {
        @Override
        public Optional<InterceptorChain> create(TypeToken type) {
            checkNotNull(type);
            return Optional.of(InterceptorChain.makeChain(new TagsInterceptor(type)));
        }
    }
}
