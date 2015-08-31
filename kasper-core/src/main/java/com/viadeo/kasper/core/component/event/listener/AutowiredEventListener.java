// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.listener;

import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.core.context.CurrentContext;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.EventBus;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 *
 * Base implementation for Kasper event listeners
 *
 * @param <E> Event
 */
public abstract class AutowiredEventListener<E extends Event>
    extends BaseEventListener<E>
    implements EventListener<E>, WirableEventListener<E>
{

    private EventBus eventBus;

    // ------------------------------------------------------------------------

    public Context getContext() {
        if (CurrentContext.value().isPresent()) {
            return CurrentContext.value().get();
        }
        throw new KasperException("Unexpected condition : no context was set during event handling");
    }

	// ------------------------------------------------------------------------

    /**
     * Publish an event on the event bus
     *
     * @param event The event
     */
    public void publish(final Event event) {
        checkNotNull(event, "The specified event must be non null");
        checkState(null != eventBus, "Unable to publish the specified event : the event bus is null");

        org.axonframework.domain.EventMessage eventMessage = GenericEventMessage.asEventMessage(event);
        this.eventBus.publish(eventMessage);
    }

    // ------------------------------------------------------------------------

    @Override
    public EventResponse handle(final EventMessage<E> message) {
        CurrentContext.set(message.getContext());
        return super.handle(message);
    }

    // ------------------------------------------------------------------------

    @Override
    public void setEventBus(final EventBus eventBus) {
        this.eventBus = checkNotNull(eventBus);
    }
}
