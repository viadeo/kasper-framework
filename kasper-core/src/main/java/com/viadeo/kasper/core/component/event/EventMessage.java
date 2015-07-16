// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import org.axonframework.domain.DomainEventMessage;
import org.joda.time.DateTime;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * Decorator for Axon Event messages
 *
 * @param <E> Encapsulated Kasper event class
 * 
 * @see EventMessage
 * @see com.viadeo.kasper.api.component.event.Event
 */
public class EventMessage<E extends Event> {
	private static final long serialVersionUID = -214545825521867826L;

	private final org.axonframework.domain.EventMessage<E> axonMessage;

	// ------------------------------------------------------------------------

	public EventMessage(final org.axonframework.domain.EventMessage<E> eventMessage) {
		this.axonMessage = checkNotNull(eventMessage);
	}

	// ------------------------------------------------------------------------

	public Context getContext() {
		return (Context) this.axonMessage.getMetaData().get(Context.METANAME);
	}

	public E getEvent() {
		return this.axonMessage.getPayload();
	}

	// ------------------------------------------------------------------------
	
	public org.axonframework.domain.EventMessage<E> getAxonMessage() {
		return this.axonMessage;
	}

    // ------------------------------------------------------------------------

    public Optional<KasperID> getEntityId() {
        if (DomainEventMessage.class.isAssignableFrom(axonMessage.getClass())) {
            final DomainEventMessage<E> domainMessage = (DomainEventMessage<E>) axonMessage;
            return Optional.of((KasperID) domainMessage.getAggregateIdentifier());
        }
        return Optional.absent();
    }

    public Long getVersion() {
        Long version = (Long) this.axonMessage.getMetaData().get(AggregateRoot.VERSION_METANAME);

        if ((null == version) && (DomainEventMessage.class.isAssignableFrom(this.axonMessage.getClass()))) {
            version = ((DomainEventMessage) this.axonMessage).getSequenceNumber();
        }

        return (null == version) ? 0L : version;
    }

    public DateTime getModificationDate() {
        return this.axonMessage.getTimestamp();
    }

    // ------------------------------------------------------------------------

    public org.axonframework.domain.EventMessage<E> axon() {
        return this.axonMessage;
    }


    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("event", getEvent())
                .add("context", getContext())
                .add("version", getVersion())
                .toString();
    }
}