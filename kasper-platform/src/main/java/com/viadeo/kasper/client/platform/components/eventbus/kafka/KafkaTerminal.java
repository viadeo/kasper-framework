// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.kafka;

import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.EventBusTerminal;

import static com.google.common.base.Preconditions.checkNotNull;

public class KafkaTerminal implements EventBusTerminal {

    private final Producer producer;
    private final Consumer consumer;

    public KafkaTerminal(
            final Producer producer,
            final Consumer consumer
    ) {
        this.consumer = consumer;
        this.producer = producer;
    }

    @Override
    public void publish(EventMessage... eventMessages) {
        for(EventMessage eventMessage:eventMessages) {
            producer.publish(eventMessage);
        }
    }

    @Override
    public void onClusterCreated(final Cluster cluster) {
        checkNotNull(cluster);
        consumer.addMessageListener(new KafkaMessageListener() {
            @Override
            public void messageReceived(final EventMessage message) {
                cluster.publish(message);
            }
        });
    }

}
