// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.step;

import com.google.common.collect.Maps;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.component.event.Event;
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
    private final Class<? extends Event> eventClass;
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
                        this.eventClass = (Class<? extends Event>) argumentType;
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

                this.eventClass = (Class<? extends Event>) (Event.class.isAssignableFrom(parameterTypes[0]) ? parameterTypes[0] : parameterTypes[1]);
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
