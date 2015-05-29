// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step;

import com.google.common.base.Objects;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.saga.Saga;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class BaseStep implements Step {

    private final Method sagaMethod;
    private final Method identifierMethod;
    private final Class<? extends Event> eventClass;

    public BaseStep(final Method method, final String getterName) {
        checkNotNull(getterName);
        this.sagaMethod = checkNotNull(method);

        checkArgument(method.getParameterTypes().length == 1, "Should specify only one and unique parameter referencing an event : " + method);

        final Class<?> eventClass = method.getParameterTypes()[0];
        checkArgument(Event.class.isAssignableFrom(eventClass), "Should specify an instance of event as parameter : " + method);

        this.eventClass = (Class<? extends Event>) eventClass;

        try {
            this.identifierMethod = this.eventClass.getMethod(getterName);
            checkArgument(identifierMethod.getParameterTypes().length == 0, "Should specify a method without parameter");
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    String.format("The specified getter name '%s' is undefined in the event '%s'", getterName, eventClass.getName()),
                    e
            );
        }
    }

    @Override
    public String name() {
        return sagaMethod.getName();
    }

    @Override
    public void invoke(final Saga saga, final Event event) {
        checkNotNull(saga);
        checkNotNull(event);

        try {
            sagaMethod.invoke(saga, event);
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public Class<? extends Event> getSupportedEvent() {
        return eventClass;
    }

    @Override
    public <T> T getSagaIdentifierFrom(final Event event) {
        checkNotNull(event);

        try {
            return (T) identifierMethod.invoke(event);
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseStep that = (BaseStep) o;

        return Objects.equal(this.name(), that.name()) &&
                Objects.equal(this.eventClass, that.eventClass);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name(), eventClass);
    }
}
