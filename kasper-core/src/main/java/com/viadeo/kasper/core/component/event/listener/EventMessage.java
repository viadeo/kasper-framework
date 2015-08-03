// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.listener;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.KasperMessage;
import org.axonframework.domain.DomainEventMessage;
import org.joda.time.DateTime;

/**
 *
 * Decorator for Axon Event messages
 *
 * @param <E> Encapsulated Kasper event class
 * 
 * @see EventMessage
 * @see com.viadeo.kasper.api.component.event.Event
 */
public class EventMessage<E extends Event> extends KasperMessage<E> {

    private final Optional<KasperID> optionalKapserID;
    private final DateTime timestamp;

    // ------------------------------------------------------------------------

	public EventMessage(final org.axonframework.domain.EventMessage<E> eventMessage) {
		this(
                (eventMessage instanceof DomainEventMessage ? Optional.fromNullable((KasperID)((DomainEventMessage) eventMessage).getAggregateIdentifier()) : Optional.<KasperID>absent()),
                eventMessage.getTimestamp(),
                Objects.firstNonNull(
                        (Context) eventMessage.getMetaData().get(Context.METANAME),
                        Contexts.empty()
                ),
                eventMessage.getPayload()
        );
	}

    public EventMessage(Optional<KasperID> entityId, DateTime timestamp, Context context, E event) {
        super(context, event);
        this.optionalKapserID = entityId;
        this.timestamp = timestamp;
    }

    // ------------------------------------------------------------------------


    public Optional<KasperID> getEntityId() {
        return optionalKapserID;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public E getEvent() {
		return getInput();
	}

    public Long getVersion() {
        return getContext().getVersion();
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("event", getEvent())
                .add("context", getContext())
                .add("version", getVersion())
                .toString();
    }
}
