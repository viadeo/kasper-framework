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

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.listener.EventDescriptor;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.SagaIdReconciler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class BaseStep implements Step {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseStep.class);

    private final Method sagaMethod;
    private final StepArguments sagaMethodArguments;
    private final Method identifierMethod;
    private final EventDescriptor eventDescriptor;
    private final ArrayList<String> actions;
    private final SagaIdReconciler reconciler;

    // ------------------------------------------------------------------------

    public BaseStep(final Method method, final String type, final String getterName, final SagaIdReconciler reconciler) {
        checkNotNull(getterName);

        this.reconciler = checkNotNull(reconciler);
        this.sagaMethod = checkNotNull(method);
        this.sagaMethodArguments = new StepArguments(sagaMethod);
        final Class<? extends Event> eventClass = this.sagaMethodArguments.getEventClass();
        this.eventDescriptor = new EventDescriptor<>(eventClass, method.isAnnotationPresent(Deprecated.class));

        try {
            this.identifierMethod = eventClass.getMethod(getterName);
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
    public void clean(Object identifier) { }

    @Override
    public EventDescriptor getSupportedEvent() {
        return eventDescriptor;
    }

    @Override
    public Optional<Object> getSagaIdentifierFrom(final Event event) {
        checkNotNull(event);

        Object identifier = null;

        try {
            identifier = identifierMethod.invoke(event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.warn("Failed to retrieve saga identifier from the given event, <getter={}> <event={}>", identifierMethod.getName(), event.getClass().getName(), e);
        }

        final Object reconciledIdentifier = reconciler.reconcile(identifier);

        return Optional.fromNullable(reconciledIdentifier);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Saga> getSagaClass() {
        return (Class<? extends Saga>) sagaMethod.getDeclaringClass();
    }

    @Override
    public Class<? extends Step> getStepClass() {
        return getClass();
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
               Objects.equal(this.eventDescriptor, that.eventDescriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name(), eventDescriptor);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("saga", sagaMethod.getDeclaringClass())
                .add("method", sagaMethod.getName())
                .add("eventDescriptor", eventDescriptor)
                .toString();
    }

}
