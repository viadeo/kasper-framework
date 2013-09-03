// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.platform.components.eventbus;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.AbstractContext;
import com.viadeo.kasper.event.Event;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.ClusteringEventBus;
import org.axonframework.eventhandling.EventBusTerminal;

import java.util.Map;

public class KasperHybridEventBus extends ClusteringEventBus {

    public KasperHybridEventBus() {
        super();
    }

    public KasperHybridEventBus(final EventBusTerminal terminal) {
        super(terminal);
    }

    public void publish(final Event event) {
        Preconditions.checkNotNull(event);
        Preconditions.checkState(event.getContext().isPresent(), "Context must be present !");

        final Context context = event.getContext().get();

        /* Sets a valid Kasper correlation id if required */
        if (AbstractContext.class.isAssignableFrom(context.getClass())) {
            final AbstractContext kasperContext = (AbstractContext) context;
            kasperContext.setValidKasperCorrelationId();
        }

        final Map<String, Object> metaData = Maps.newHashMap();
        metaData.put(Context.METANAME, Preconditions.checkNotNull(context));

        final GenericEventMessage<Event> eventMessageAxon =
                new GenericEventMessage<>(event, metaData);

        this.publish(eventMessageAxon);
    }

    public void publish(final Event event, final Context context) {
        Preconditions.checkNotNull(event).setContext(context);
        this.publish(event);
    }
}
