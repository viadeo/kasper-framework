// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.step;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.listener.EventDescriptor;
import com.viadeo.kasper.core.component.event.saga.Saga;

import java.util.List;

/**
 * part of <code>Saga</code>'s lifecycle
 */
public interface Step {

    /**
     * @return the <code>Step</code>'s name
     */
    String name();

    /**
     * invoke the <code>Saga</code>'s <code>Step</code>
     *
     * @param saga the instance of saga
     * @param context the <code>Context</code>
     * @param event the <code>Event</code>
     * @throws StepInvocationException if an error occurs during invocation
     */
    void invoke(Saga saga, Context context, Event event) throws StepInvocationException;

    /**
     * clean all things to be cleaned before storing <code>Saga</code>
     *
     * @param identifier the saga identifier
     */
    void clean(Object identifier);

    /**
     * get the <code>Step</code>'s <code>Event</code> Class
     *
     * @return supported event by this <code>Step</code>
     */
    EventDescriptor getSupportedEvent();

    /**
     * retrieve the <code>Saga</code> identifier from the given <code>Event</code>
     *
     * @param event an <code>Event</code>
     * @param <T> the identifier type
     * @return the optional identifier
     */
    <T> Optional<T> getSagaIdentifierFrom(Event event);

    /**
     * get the <code>Saga</code> Class
     *
     * @return the <code>Saga</code> Class
     */
    Class<? extends Saga> getSagaClass();

    /**
     * get the <code>Step</code> Class
     *
     * @return the <code>Step</code> Class
     */
    Class<? extends Step> getStepClass();

    /**
     * @return a list of identifier accessor
     */
    List<String> getActions();
}
