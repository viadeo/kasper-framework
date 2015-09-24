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

    private final Map<Class<?>,Handler> handlerByEventClasses;

    public MultiEventListener() {
        this.handlerByEventClasses = Maps.newHashMap();

        for (final Handler handler : discoverHandlers()) {
            this.handlerByEventClasses.put(handler.getEventClass(), handler);
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

    public final EventResponse handle(final Context context, final Event event) {
        for (Class<?> eventClass : handlerByEventClasses.keySet()) {
            if (eventClass.isAssignableFrom(event.getClass())) {
                return handlerByEventClasses.get(event.getClass()).handle(context, event);
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
    public Set<Class<?>> getEventClasses() {
        return handlerByEventClasses.keySet();
    }

    @VisibleForTesting
    protected Map<Class<?>, Handler> getHandlerByEventClasses() {
        return handlerByEventClasses;
    }

    // ------------------------------------------------------------------------

    @Retention(RetentionPolicy.RUNTIME)
    @Target(value={METHOD})
    public static @interface Handle { }

    // ------------------------------------------------------------------------

    protected static class Handler {

        private final Method method;
        private final Object instance;
        private Class<?> eventClass;

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
                return EventResponse.failure(new KasperReason(
                        CoreReasonCode.INTERNAL_COMPONENT_ERROR,
                        String.format("Error during handling event by the method '%s', <handler=%s>", method.getName(), instance.getClass().getName())
                ));
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

        public Class<?> getEventClass() {
            if (eventClass == null) {
                for (final Class<?> parameterClass : method.getParameterTypes()) {
                    if (Event.class.isAssignableFrom(parameterClass)) {
                        eventClass = parameterClass;
                        break;
                    }
                }
            }
            return eventClass;
        }
    }
}
