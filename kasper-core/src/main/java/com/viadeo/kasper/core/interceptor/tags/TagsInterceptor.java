// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
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

public class TagsInterceptor<I,O> implements Interceptor<I,O> {

    private final Class<?> target;

    public TagsInterceptor(final TypeToken type) {
        checkNotNull(type);
        target = type.getRawType();
    }

    @SuppressWarnings("unchecked")
    @Override
    public O process(I c, Context context, InterceptorChain<I, O> chain) {
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

    public static class Factory<I,O> implements InterceptorFactory<I,O> {
        @Override
        public Optional<InterceptorChain<I,O>> create(TypeToken type) {
            checkNotNull(type);
            return Optional.of(InterceptorChain.<I,O>makeChain(new TagsInterceptor<I,O>(type)));
        }
    }
}
