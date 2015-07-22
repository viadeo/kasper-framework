// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.tags;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Tags;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorFactory;
import org.slf4j.MDC;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class TagsInterceptor<C> implements Interceptor<C, Object> {

    private final Class<?> target;

    public TagsInterceptor(final TypeToken type) {
        checkNotNull(type);
        target = type.getRawType();
    }

    @Override
    public Object process(C c, Context context, InterceptorChain<C, Object> chain) throws Exception {
        checkNotNull(context);

        final Set<String> additionalTags = TagsHolder.getTags(target);
        final Context newContext = new Context.Builder(context)
                .addTags(additionalTags)
                .build();

        final Map originalContextMap = Objects.firstNonNull(MDC.getCopyOfContextMap(), Collections.emptyMap());

        MDC.setContextMap(newContextMapWithAdditionalTags(originalContextMap, additionalTags));
        try {
            return chain.next(c, newContext);
        } finally {
            MDC.setContextMap(originalContextMap);
        }
    }

    @SuppressWarnings("unchecked")
    private Map newContextMapWithAdditionalTags(Map initialContextMap, Set<String> additionalTags) {
        final String initialTags = Strings.nullToEmpty((String) initialContextMap.get(Context.TAGS_SHORTNAME));
        final Set<String> allTags = Sets.union(Tags.valueOf(initialTags), additionalTags);

        final Map contextMap = Maps.newHashMap(initialContextMap);
        contextMap.put(Context.TAGS_SHORTNAME, Tags.toString(allTags));
        return contextMap;
    }

    public static class Factory implements InterceptorFactory {
        @Override
        public Optional<InterceptorChain> create(TypeToken type) {
            checkNotNull(type);
            return Optional.of(InterceptorChain.makeChain(new TagsInterceptor(type)));
        }
    }
}
