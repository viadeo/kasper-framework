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
package com.viadeo.kasper.core.component.event.listener;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.common.tools.ReflectionGenericsResolver;

import java.util.Set;

public class BaseEventListener<EVENT extends Event>
    extends AxonEventListener<EVENT>
    implements EventListener<EVENT>
{

    private final Class<EVENT> eventClass;

    public BaseEventListener() {
        super();
        eventClass = init();
    }

    protected Class<EVENT> init() {
        @SuppressWarnings("unchecked")
        final Optional<Class<EVENT>> eventClassOpt =
                (Optional<Class<EVENT>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                this.getClass(),
                                EventListener.class,
                                EventListener.EVENT_PARAMETER_POSITION
                        );

        if (eventClassOpt.isPresent()) {
            return eventClassOpt.get();
        } else {
            throw new KasperException("Unable to identify event class for " + this.getClass());
        }
    }

    @Override
    public EventResponse handle(final EventMessage<EVENT> message) {
        return handle(message.getContext(), message.getEvent());
    }

    public EventResponse handle(final Context context, final EVENT event) {
        throw new UnsupportedOperationException("not yet implemented!");
    }


    @Override
    public void rollback(final EventMessage<EVENT> message) {
        rollback(message.getContext(), message.getInput());
    }

    public void rollback(final Context context, final Event event) {
        // nothing
    }

    @Override
    public Class<EVENT> getInputClass() {
        return eventClass;
    }

    @Override
    public Class<?> getHandlerClass() {
        return getClass();
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<EventDescriptor> getEventDescriptors() {
        return Sets.newHashSet(new EventDescriptor(this.eventClass, getClass().isAnnotationPresent(Deprecated.class)));
    }

    @Override
    public String toString() {
        return getName();
    }
}
