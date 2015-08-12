// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.step;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.Saga;

import java.util.List;

/**
 * part of <code>Saga</code>'s lifecycle
 */
public interface Step {

    /**
     * <code>Step</code>'s name
     * @return
     */
    String name();

    /**
     * invoke the <code>Saga</code>' <code>Step</code>
     * @param saga
     * @param context
     * @param event
     * @throws StepInvocationException
     */
    void invoke(Saga saga, Context context, Event event) throws StepInvocationException;

    /**
     * clean all things to be cleaned before storing <code>Saga</code>
     *
     * @param identifier
     */
    void clean(Object identifier);

    /**
     * get the <code>Step</code>'s <code>Event</code> Class
     *
     * @return
     */
    Class<? extends Event> getSupportedEvent();

    /**
     * retrieve the <code>Saga</code> identifier from the given <code>Event</code>
     *
     * @param event
     * @param <T>
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
     * list of identifier accessor
     * @return
     */
    List<String> getActions();
}
