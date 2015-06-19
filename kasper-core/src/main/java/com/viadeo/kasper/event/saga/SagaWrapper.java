// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.event.AxonEventListener;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventResponse;
import com.viadeo.kasper.event.IEventListener;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class SagaWrapper extends AxonEventListener<Event> implements IEventListener {

    private final SagaExecutor executor;

    public SagaWrapper(SagaExecutor executor) {
        this.executor = checkNotNull(executor);
    }

    @Override
    public EventResponse handle(com.viadeo.kasper.event.EventMessage message) {
        checkNotNull(message);
        return handle(message.getContext(), message.getEvent());
    }

    @Override
    public EventResponse handle(Context context, Event event) {
        try {
            // TODO delegate the execution of an event with the context
            executor.execute(event);
        } catch (Exception e) {
            return EventResponse.error(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, e));
        }
        return EventResponse.success();
    }

    @Override
    public Set<Class<?>> getEventClasses() {
        return executor.getEventClasses();
    }
}
