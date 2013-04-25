// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.viadeo.kasper.event.IEvent;
import com.viadeo.kasper.event.annotation.XKasperEvent;

/**
 *
 * Process Kasper event dynamic registration at platform boot
 *
 * @see XKasperEvent
 */
public class EventsDocumentationProcessor extends AbstractDocumentationProcessor<XKasperEvent, IEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventsDocumentationProcessor.class);

	// ------------------------------------------------------------------------

	/**
	 * Process Kasper event
	 * 
	 * @see IEvent
	 * @see com.viadeo.kasper.core.boot.IAnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void process(final Class<?> eventClazz) {
		LOGGER.info("Record on event library : " + eventClazz.getName());

		getKasperLibrary().recordEvent((Class<? extends IEvent>) eventClazz);		
	}

}

