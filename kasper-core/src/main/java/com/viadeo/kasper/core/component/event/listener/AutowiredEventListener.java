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
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;

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
    implements WirableEventListener<E>
{

    private KasperEventBus eventBus;

	// ------------------------------------------------------------------------

    /**
     * Publish an event on the event bus
     *
     * @param event The event
     */
    public void publish(final Context context, final Event event) {
        checkNotNull(event, "The specified event must be non null");
        checkNotNull(context, "The specified context must be non null");
        checkState(null != eventBus, "Unable to publish the specified event : the event bus is null");

        this.eventBus.publish(context, event);
    }

    // ------------------------------------------------------------------------

    @Override
    public EventResponse handle(final EventMessage<E> message) {
        return super.handle(message);
    }

    // ------------------------------------------------------------------------

    @Override
    public void setEventBus(final KasperEventBus eventBus) {
        this.eventBus = checkNotNull(eventBus);
    }
}
