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
package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.collect.Sets;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.core.component.event.listener.EventDescriptor;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class ReflectionRoutingKeysResolver implements RoutingKeysResolver {

    private final List<Class<Event>> rootClasses = Arrays.asList(Event.class);
    private static final Reflections reflections = new Reflections();

    @Override
    public RoutingKeys resolve(org.axonframework.eventhandling.EventListener eventListener) {

        if (!(eventListener instanceof EventListener)) {
            throw new IllegalArgumentException("routing key has to be an instance of " + EventListener.class.getName());
        }

        final Set<EventDescriptor> eventDescriptors = ((EventListener) eventListener).getEventDescriptors();
        final Set<RoutingKeys.RoutingKey> routingKeys = Sets.newHashSet();

        for (final EventDescriptor eventDescriptor : eventDescriptors) {
            final Class eventClass = eventDescriptor.getEventClass();

            if (rootClasses.contains(eventClass)) {
                routingKeys.add(new RoutingKeys.RoutingKey("#", eventDescriptor.isDeprecated()));
            } else {
                routingKeys.add(new RoutingKeys.RoutingKey(eventClass.getName(), eventDescriptor.isDeprecated()));
                final Set subTypes = reflections.getSubTypesOf(eventClass);
                for (final Object type : subTypes) {
                    routingKeys.add(new RoutingKeys.RoutingKey(((Class) type).getName(), eventDescriptor.isDeprecated()));
                }
            }
        }

        return new RoutingKeys(routingKeys);
    }
}
