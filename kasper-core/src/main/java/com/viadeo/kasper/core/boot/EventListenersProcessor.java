// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.annotation.XKasperEventListener;
import org.axonframework.eventhandling.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Process Kasper event listeners dynamic registration at kasper platform boot
 *
 * @see XKasperEventListener
 */
public class EventListenersProcessor extends SingletonAnnotationProcessor<XKasperEventListener, org.axonframework.eventhandling.EventListener> {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventListenersProcessor.class);
	
	/**
	 * The event bus to register event listeners on
	 */
	private transient EventBus eventBus;
    private transient CommandGateway commandGateway;
	
	// ------------------------------------------------------------------------
	
	/**
	 * Process Kasper event listener
	 * 
	 * @see org.axonframework.eventhandling.EventListener
	 * @see AnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	public void process(final Class eventListenerClazz, final org.axonframework.eventhandling.EventListener eventListener) {
		LOGGER.info("Subscribe to event bus : " + eventListenerClazz.getName());

        if (EventListener.class.isAssignableFrom(eventListener.getClass())) {
            ((EventListener) eventListener).setCommandGateway(this.commandGateway);
        }

		//- Subscribe the listener to the event bus (Axon) --------------------
		eventBus.subscribe(eventListener);
	}

	// ------------------------------------------------------------------------
	
	/**
	 * @param eventBus the event bus to register event listeners on
	 */
	public void setEventBus(final EventBus eventBus) {
		this.eventBus = Preconditions.checkNotNull(eventBus);
	}

    public void setCommandGateway(final CommandGateway commandGateway) {
        this.commandGateway = Preconditions.checkNotNull(commandGateway);
    }
	
}
