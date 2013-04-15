// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.core.boot;

import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.event.IEventListener;
import com.viadeo.kasper.event.annotation.XKasperEventListener;

/**
 *
 * Process Kasper event listeners dynamic registration at platform boot
 *
 * @see XKasperEventListener
 */
public class EventListenersProcessor extends AbstractSingletonAnnotationProcessor<XKasperEventListener, EventListener> {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventListenersProcessor.class);	
	
	/**
	 * The event bus to register event listeners on
	 */
	private transient EventBus eventBus;
	
	// ------------------------------------------------------------------------
	
	/**
	 * Process Kasper event listener
	 * 
	 * @see IEventListener
	 * @see com.viadeo.kasper.core.boot.IAnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	public void process(final Class<?> eventListenerClazz, final EventListener eventListener) {		
		LOGGER.info("Subscribe to event bus : " + eventListenerClazz.getName());
		
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
	
}

	