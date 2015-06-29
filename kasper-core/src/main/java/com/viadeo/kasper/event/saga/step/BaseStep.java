// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.saga.Saga;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class BaseStep implements Step {

    private final Method sagaMethod;
    private final StepArguments sagaMethodArguments;
    private final Method identifierMethod;
    private final Class<? extends Event> eventClass;
    private final ArrayList<String> actions;

    // ------------------------------------------------------------------------

    public BaseStep(final Method method, final String type, final String getterName) {
        checkNotNull(getterName);

        this.sagaMethod = checkNotNull(method);
        this.sagaMethodArguments = new StepArguments(sagaMethod);
        this.eventClass = this.sagaMethodArguments.getEventClass();

        try {
            this.identifierMethod = this.eventClass.getMethod(getterName);
            checkArgument(identifierMethod.getParameterTypes().length == 0, "Should specify a method without parameter");
        } catch (final NoSuchMethodException e) {
            throw new IllegalArgumentException(
                String.format("The specified getter name '%s' is undefined in the event '%s': %s", getterName, eventClass.getName(), method.getName()),
                e
            );
        }

        this.actions = Lists.newArrayList(
                String.format("%s(getter=%s)", type, getterName)
        );
    }

    // ------------------------------------------------------------------------


    @Override
    public List<String> getActions() {
        return actions;
    }

    @Override
    public String name() {
        return sagaMethod.getName();
    }

    @Override
    public void invoke(final Saga saga, final Context context, final Event event) throws StepInvocationException {
        checkNotNull(saga);
        checkNotNull(context);
        checkNotNull(event);

        try {
            sagaMethod.invoke(saga, sagaMethodArguments.order(context, event));
        } catch (final Exception e) {
            throw new StepInvocationException(
                String.format(
                    "Error in invoking step, <step=%s> <method=%s> <payload=%s>",
                    getClass().getSimpleName(), sagaMethod.getName(), event
                ),
                e
            );
        }
    }

    @Override
    public Class<? extends Event> getSupportedEvent() {
        return eventClass;
    }

    @Override
    public <T> Optional<T> getSagaIdentifierFrom(final Event event) {
        checkNotNull(event);

        Object identifier =null;

        try {
            identifier = identifierMethod.invoke(event);
        } catch (final IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (final InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return Optional.fromNullable((T)identifier);
    }

    @Override
    public Class<? extends Saga> getSagaClass() {
        return (Class<? extends Saga>) sagaMethod.getDeclaringClass();
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (null == o || getClass() != o.getClass()) {
            return false;
        }

        final BaseStep that = (BaseStep) o;

        return Objects.equal(this.name(), that.name()) &&
               Objects.equal(this.eventClass, that.eventClass);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name(), eventClass);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("saga", sagaMethod.getDeclaringClass())
                .add("method", sagaMethod.getName())
                .add("event", eventClass)
                .toString();
    }

}
