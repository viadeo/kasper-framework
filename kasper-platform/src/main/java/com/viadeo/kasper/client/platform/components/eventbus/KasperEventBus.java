// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.context.CurrentContext;
import com.viadeo.kasper.event.IEvent;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.ClusterSelector;
import org.axonframework.eventhandling.ClusteringEventBus;
import org.axonframework.eventhandling.EventBusTerminal;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.getMetricRegistry;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

/*
 * Default Kasper event bus based on Axon's Cluster
 *
 * FIXME: work in progress
 * FIXME: Work on better configuration handling and different default policies
 *
 */
public class KasperEventBus extends ClusteringEventBus {

    private static final String GLOBAL_METER_EVENTS_NAME = name(KasperEventBus.class, "events");

    public KasperEventBus(ClusterSelector clusterSelector) {
        super(clusterSelector);
    }

    public KasperEventBus(final ClusterSelector clusterSelector, final EventBusTerminal terminal) {
        super(clusterSelector, terminal);
    }

    @Override
    public void publish(final EventMessage... messages) {
        getMetricRegistry().meter(GLOBAL_METER_EVENTS_NAME).mark();

        final EventMessage[] newMessages;

        /* Add the context to messages if required */
        if (CurrentContext.value().isPresent()) {
            newMessages = new EventMessage[messages.length];
            for (int i = 0 ; i < messages.length ; i++) {
                final EventMessage message = messages[i];
                if ( ! message.getMetaData().containsKey(Context.METANAME)) {
                    final Map<String, Object> metaData = Maps.newHashMap();
                    metaData.put(Context.METANAME, CurrentContext.value().get());
                    newMessages[i] = message.andMetaData(metaData);
                } else {
                    newMessages[i] = message;
                }
            }
        } else {
            newMessages = messages;
        }

        this.publishToSuper(newMessages);
    }

    @VisibleForTesting
    void publishToSuper(final EventMessage... messages) {
        super.publish(messages);
    }

    public void publish(final IEvent event) {
        this.publish(GenericEventMessage.asEventMessage(event));
    }

    public void publishEvent(final Context context, final IEvent event) {
        this.publish(
            new GenericEventMessage<>(
                checkNotNull(event),
                new HashMap<String, Object>() {{
                    this.put(Context.METANAME, context);
                }}
            )
        );
    }
}
