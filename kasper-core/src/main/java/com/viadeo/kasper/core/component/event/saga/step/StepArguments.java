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
package com.viadeo.kasper.core.component.event.saga.step;

import com.google.common.collect.Maps;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.listener.EventMessage;
import org.axonframework.domain.GenericEventMessage;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class StepArguments {

    private final Map<Class, Integer> parameterTypes;
    private final Class eventClass;
    private final Boolean useEventMessage;

    // ------------------------------------------------------------------------

    public StepArguments(final Method method) {
        this.parameterTypes = Maps.newHashMap();

        if (method.getParameterTypes().length == 1) {

            final Class parameterClass = method.getParameterTypes()[0];

            if (Event.class.isAssignableFrom(parameterClass)) {

                this.parameterTypes.put(parameterClass, 0);
                this.eventClass = parameterClass;
                this.useEventMessage = Boolean.FALSE;

            } else if (EventMessage.class.isAssignableFrom(parameterClass)) {

                this.parameterTypes.put(parameterClass, 0);
                this.useEventMessage = Boolean.TRUE;

                final Type parameterType = method.getGenericParameterTypes()[0];

                if (parameterType instanceof ParameterizedType) {
                    final ParameterizedType type = (ParameterizedType) parameterType;
                    final Type argumentType = type.getActualTypeArguments()[0];

                    if (argumentType instanceof Class) {
                        this.eventClass = (Class) argumentType;
                    } else {
                        this.eventClass = Event.class;
                    }
                } else {
                    this.eventClass = Event.class;
                }

            } else {
                throw new IllegalArgumentException(String.format(
                    "Illegal method definition '%s', definitions with only one parameter can support one of the following types : [%s|%s]",
                    method, Event.class.getName(), EventMessage.class.getName()
                ));
            }

        } else if (method.getParameterTypes().length == 2) {

            final Class<?>[] parameterTypes = method.getParameterTypes();

            if ((Event.class.isAssignableFrom(parameterTypes[0]) && Context.class.isAssignableFrom(parameterTypes[1])) ||
                    (Event.class.isAssignableFrom(parameterTypes[1]) && Context.class.isAssignableFrom(parameterTypes[0]))) {

                this.eventClass = (Class) (Event.class.isAssignableFrom(parameterTypes[0]) ? parameterTypes[0] : parameterTypes[1]);
                this.useEventMessage = Boolean.FALSE;

                int position = 0;
                for (final Class<?> parameterType : parameterTypes) {
                    this.parameterTypes.put(parameterType, position++);
                }

            } else {
                throw new IllegalArgumentException(String.format(
                    "Illegal method definition '%s', definitions with two parameters require the following type : %s and %s",
                    method, Event.class.getName(), Context.class.getName()
                ));
            }

        } else {
            throw new IllegalArgumentException(
                String.format("Illegal method definition '%s', supported method definitions are : <Event>|<Event,Context>|<EventMessage>", method)
            );
        }
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Class<? extends Event> getEventClass() {
        return eventClass;
    }

    public Object[] order(final Context context, final Event event) {
        checkNotNull(context);
        checkNotNull(event);

        final Object[] arguments = new Object[parameterTypes.size()];

        if (useEventMessage) {
            arguments[0] = createEventMessage(context, event);
        } else {
            for (final Object argument : new Object[]{context, event}) {
                final Integer index = parameterTypes.get(argument.getClass());

                if (null != index) {
                    arguments[index] = argument;
                }
            }
        }

        return arguments;
    }

    @SuppressWarnings("unchecked")
    private EventMessage createEventMessage(final Context context, final Event event) {
        return new EventMessage(
            new GenericEventMessage(
                event,
                new HashMap<String, Object>() {{
                    this.put(Context.METANAME, context);
                }}
            )
        );
    }

}
