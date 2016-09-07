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
