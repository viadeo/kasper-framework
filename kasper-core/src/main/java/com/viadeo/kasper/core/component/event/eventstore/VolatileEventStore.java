
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.eventstore;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.axonframework.domain.DomainEventMessage;
import org.axonframework.domain.DomainEventStream;
import org.axonframework.domain.SimpleDomainEventStream;
import org.axonframework.eventstore.EventStore;

import java.util.ArrayList;

public class VolatileEventStore implements EventStore {

    static class AggregateTypedEventMessage {
        String type;
        DomainEventMessage<?> eventMessage;
    }

    private final CircularFifoBuffer queue;


    public VolatileEventStore(final int limit) {
        this.queue = new CircularFifoBuffer(limit);
    }

    @Override
    public synchronized void appendEvents(String type, DomainEventStream events) {
        while (events.hasNext()) {
            AggregateTypedEventMessage obj = new AggregateTypedEventMessage();
            obj.type = type;
            obj.eventMessage = events.next();
            queue.add(obj);
        }
    }

    @Override
    public synchronized DomainEventStream readEvents(String type, Object identifier) {
        ArrayList<DomainEventMessage<?>> selection = new ArrayList<>();
        for (Object o : queue) {
            AggregateTypedEventMessage typedMessage = (AggregateTypedEventMessage)o;
            if (typedMessage.type.equals(type)) {
                DomainEventMessage<?> evMsg = typedMessage.eventMessage;
                if (identifier.equals(evMsg.getAggregateIdentifier())) {
                    selection.add(typedMessage.eventMessage);
                }
            }
        }

        return new SimpleDomainEventStream(selection);
    }
}
