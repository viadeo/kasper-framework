// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Process Kasper event dynamic registration at platform boot
 *
 * @see XKasperEvent
 */
public class EventsDocumentationProcessor extends DocumentationProcessor<XKasperEvent, Event> {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventsDocumentationProcessor.class);

	// ------------------------------------------------------------------------

    /**
     * Annotation is optional for events
     */
    public boolean isAnnotationMandatory() {
        return false;
    }

    /**
	 * Process Kasper event
	 * 
	 * @see com.viadeo.kasper.event.Event
	 * @see AnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void process(final Class<?> eventClazz) {
		LOGGER.info("Record on event library : " + eventClazz.getName());

		getKasperLibrary().recordEvent((Class<? extends Event>) eventClazz);
	}

}

