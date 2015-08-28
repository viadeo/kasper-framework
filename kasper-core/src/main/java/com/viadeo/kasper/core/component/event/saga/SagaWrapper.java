// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga;

import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.core.component.event.listener.AxonEventListener;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.event.listener.EventMessage;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class SagaWrapper extends AxonEventListener<Event> implements EventListener<Event> {

    private final SagaExecutor executor;

    // ------------------------------------------------------------------------

    public SagaWrapper(final SagaExecutor executor) {
        super();
        this.executor = checkNotNull(executor);
    }

    // ------------------------------------------------------------------------

    @Override
    public EventResponse handle(final EventMessage<Event> message) {
        try {
            executor.execute(message.getContext(), message.getInput());
        } catch (final Exception e) {
            return EventResponse.error(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, e));
        }
        return EventResponse.success();
    }

    @Override
    public void rollback(final EventMessage<Event> message) {
        // nothing
    }

    @Override
    public String getName() {
        return executor.getSagaClass().getName();
    }

    @Override
    public Set<Class<?>> getEventClasses() {
        return executor.getEventClasses();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Class<Event> getInputClass() {
        return Event.class;
    }

    @Override
    public Class<?> getHandlerClass() {
        return executor.getSagaClass();
    }
}
