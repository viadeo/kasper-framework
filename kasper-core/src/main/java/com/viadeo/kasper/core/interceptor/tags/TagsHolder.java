// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.tags;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.annotation.XKasperEventListener;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public final class TagsHolder {

    private TagsHolder() {
    }

    public static final TagsHolder INSTANCE = new TagsHolder();

    public static Set<String> getTags(Class<?> target) {
        return INSTANCE.retrieveTags(target);
    }

    @VisibleForTesting
    protected static final Map<Class<?>, Set<String>> CACHE_TAGS = Maps.newHashMap();

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

            final XKasperEventListener eventAnnotation = handlerClass.getAnnotation(XKasperEventListener.class);
            if (null != eventAnnotation) {
                tags.addAll(ImmutableSet.copyOf(eventAnnotation.tags()));
            }

            CACHE_TAGS.put(handlerClass, tags);

        } else {
            tags = CACHE_TAGS.get(handlerClass);
        }

        return tags;
    }

}
