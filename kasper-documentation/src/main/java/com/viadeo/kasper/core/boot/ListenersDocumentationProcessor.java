// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.annotation.XKasperEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Process Kasper listener dynamic registration at platform boot
 *
 */
public class ListenersDocumentationProcessor extends DocumentationProcessor<XKasperEventListener, EventListener<?>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ListenersDocumentationProcessor.class);

	// ------------------------------------------------------------------------

	/**
	 * Process Kasper listener
	 * 
	 * @see com.viadeo.kasper.event.EventListener
	 * @see AnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void process(final Class<?> listenerClazz) {
		LOGGER.info("Record on listener library : " + listenerClazz.getName());
		
		//- Register the domain to the locator --------------------------------
		getKasperLibrary().recordListener((Class<? extends EventListener<?>>) listenerClazz);
	}
	
}

