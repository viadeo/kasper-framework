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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Implements this class when you need to listen to different events.
 * Keep in mind that a listener should only provide one business rule ie. has only one responsibility.
 *
 * Use <code>Handle</code> annotation to mark a method that handles an event.
 *
 * The annotated method must have this definition:
 * <pre>public EventResponse myListener(&lt;Event event| Context context, Event event| Event event, Context context&gt;) {...}</pre>
 *
 * @see Handle
 */
public class MultiEventListener extends BaseEventListener<Event> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiEventListener.class);

    private final Map<EventDescriptor,Handler> handlerByEventClasses;

    public MultiEventListener() {
        this.handlerByEventClasses = Maps.newHashMap();

        for (final Handler handler : discoverHandlers()) {
            this.handlerByEventClasses.put(
                    handler.getEventDescriptor(),
                    handler
            );
        }

        // TODO what's happen if we have no discovered handlers here?
    }

    protected List<Handler> discoverHandlers() {
        final List<Handler> discoveredHandlers = Lists.newArrayList();

        for (final Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Handle.class)) {
                if ( ! checkParameters(method)) {
                    LOGGER.error(
                            "Bad handler definition: bad parameters, <eventListener={}> <method={}> <parameters={}>",
                            method.getDeclaringClass().getSimpleName(),
                            method.getName(),
                            Lists.newArrayList(method.getParameterTypes())
                    );
                    continue;
                }

                if ( ! checkReturnType(method)) {
                    LOGGER.error("return type");

                    LOGGER.error(
                            "Bad handler definition: must return an EventResponse, <eventListener={}> <method={}>",
                            method.getDeclaringClass().getSimpleName(),
                            method.getName()
                    );
                    continue;
                }

                discoveredHandlers.add(new Handler(method, this));
            }
        }

        return discoveredHandlers;
    }

    protected boolean checkParameters(Method method) {
        final List<Class> eventClasses = Lists.newArrayList();
        final List<Class> contextClasses = Lists.newArrayList();
        final List<Class> otherClasses = Lists.newArrayList();

        for (final Class<?> parameterClass : method.getParameterTypes()) {
            if (Event.class.isAssignableFrom(parameterClass)) {
                eventClasses.add(parameterClass);
            } else if (Context.class.isAssignableFrom(parameterClass)) {
                contextClasses.add(parameterClass);
            } else {
                otherClasses.add(parameterClass);
            }
        }
        return eventClasses.size() == 1 && contextClasses.size() <=1 && otherClasses.size() == 0;
    }

    protected boolean checkReturnType(Method method) {
        return EventResponse.class.isAssignableFrom(method.getReturnType());
    }

    @SuppressWarnings("unchecked")
    public final EventResponse handle(final Context context, final Event event) {
        for (final EventDescriptor eventDescriptor : handlerByEventClasses.keySet()) {
            if (eventDescriptor.isDeprecated()) {
                return EventResponse.error(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, "Unexpected event : the handler is deprecated"));

            } else if (eventDescriptor.getEventClass().isAssignableFrom(event.getClass())) {
                return handlerByEventClasses.get(eventDescriptor).handle(context, event);
            }
        }
        return EventResponse.failure(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, "Unexpected event "));
    }

    @Deprecated
    @Override
    public Class<Event> getInputClass() {
        return super.getInputClass();
    }

    @Override
    public Set<EventDescriptor> getEventDescriptors() {
        return handlerByEventClasses.keySet();
    }

    @VisibleForTesting
    protected Map<EventDescriptor, Handler> getHandlerByEventClasses() {
        return handlerByEventClasses;
    }

    // ------------------------------------------------------------------------

    @Retention(RetentionPolicy.RUNTIME)
    @Target(value={METHOD})
    public @interface Handle { }

    // ------------------------------------------------------------------------

    protected static class Handler {

        private final Method method;
        private final Object instance;
        private EventDescriptor eventDescriptor;

        public Handler(final Method method, final Object instance) {
            this.method = checkNotNull(method);
            this.instance = checkNotNull(instance);
        }

        public EventResponse handle(final Context context, final Event event) {
            checkNotNull(context);
            checkNotNull(event);
            try {
                return (EventResponse) method.invoke(instance, parameters(context, event));
            } catch (IllegalAccessException | InvocationTargetException e) {
                return EventResponse.failure(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, e));
            }
        }

        protected Object[] parameters(final Context context, final Event event) {
            final Class<?>[] parameterTypes = method.getParameterTypes();
            final Object[] objects = new Object[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; i++) {
                if (Context.class.isAssignableFrom(parameterTypes[i])) {
                    objects[i] = context;
                } else if (Event.class.isAssignableFrom(parameterTypes[i])) {
                    objects[i] = event;
                } else {
                    throw new KasperException(String.format("Error during handling event by the method '%s' : Unexpected parameters type, <parameters=%s>", method.getName(), Lists.newArrayList(parameterTypes)));
                }
            }

            return objects;
        }

        @SuppressWarnings("unchecked")
        public EventDescriptor getEventDescriptor() {
            if (eventDescriptor == null) {
                for (final Class<?> parameterClass : method.getParameterTypes()) {
                    if (Event.class.isAssignableFrom(parameterClass)) {
                        eventDescriptor = new EventDescriptor(parameterClass, method.isAnnotationPresent(Deprecated.class));
                        break;
                    }
                }
            }
            return eventDescriptor;
        }
    }
}
