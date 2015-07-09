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
import com.viadeo.kasper.event.*;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class SagaWrapper extends AxonEventListener<Event> implements IEventListener {

    private final SagaExecutor executor;

    // ------------------------------------------------------------------------

    public SagaWrapper(final SagaExecutor executor) {
        this.executor = checkNotNull(executor);
    }

    // ------------------------------------------------------------------------

    @Override
    public EventResponse handle(final EventMessage message) {
        checkNotNull(message);
        return handle(message.getContext(), message.getEvent());
    }

    @Override
    public EventResponse handle(final Context context, final Event event) {
        try {
            executor.execute(context, event);
        } catch (final Exception e) {
            return EventResponse.error(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, e));
        }
        return EventResponse.success();
    }

    @Override
    public String getName() {
        return executor.getSagaClass().getName();
    }

    @Override
    public Set<Class<?>> getEventClasses() {
        return executor.getEventClasses();
    }

}
