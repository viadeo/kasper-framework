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
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.common.tools.ReflectionGenericsResolver;
import com.viadeo.kasper.core.component.annotation.XKasperEventListener;
import com.viadeo.kasper.core.component.event.listener.EventListener;

import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class EventListenerResolver extends AbstractResolver<EventListener> {

    private static ConcurrentMap<Class, Class> cacheEvents = Maps.newConcurrentMap();

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "EventListener";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends EventListener> clazz) {

        if (DOMAINS_CACHE.containsKey(checkNotNull(clazz))) {
            return Optional.<Class<? extends Domain>>of(DOMAINS_CACHE.get(clazz));
        }

        final XKasperEventListener eventAnnotation = clazz.getAnnotation(XKasperEventListener.class);

        if (null != eventAnnotation) {
            final Class<? extends Domain> domain = eventAnnotation.domain();
            DOMAINS_CACHE.put(clazz, domain);
            return Optional.<Class<? extends Domain>>of(domain);
        }

        return Optional.absent();
    }

    @Override
    public String getLabel(final Class<? extends EventListener> clazz) {
        return checkNotNull(clazz).getSimpleName()
                .replace("QueryEventListener", "")
                .replace("CommandEventListener", "")
                .replace("QueryListener", "")
                .replace("CommandListener", "")
                .replace("EventListener", "")
                .replace("Listener", "");
    }

    // ------------------------------------------------------------------------

    @Override
    public String getDescription(final Class<? extends EventListener> clazz) {
        final XKasperEventListener annotation =
                checkNotNull(clazz).getAnnotation(XKasperEventListener.class);

        String description = "";

        if (null != annotation) {
            description = annotation.description();
        }

        if (description.isEmpty()) {
            description = String.format("The %s event listener", this.getLabel(clazz));
        }

        return description;
    }

    // ------------------------------------------------------------------------

//    @SuppressWarnings("unchecked")
    public Class<? extends Event> getEventClass(final Class<? extends EventListener> clazz) {

        if (cacheEvents.containsKey(checkNotNull(clazz))) {
            return cacheEvents.get(clazz);
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends Event>> eventClazz =
                (Optional<Class<? extends Event>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz,
                                EventListener.class,
                                EventListener.EVENT_PARAMETER_POSITION
                        );

        if ( ! eventClazz.isPresent()) {
            throw new KasperException("Unable to find event type for listener " + clazz.getClass());
        }

        cacheEvents.put(clazz, eventClazz.get());

        return eventClazz.get();
    }

    // ------------------------------------------------------------------------

    @Override
    public void clearCache() {
        cacheEvents.clear();
    }

}
