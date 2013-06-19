// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Process Kasper handler dynamic registration at platform boot
 *
 */
public class HandlersDocumentationProcessor extends DocumentationProcessor<XKasperCommandHandler, CommandHandler<?>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(HandlersDocumentationProcessor.class);

	// ------------------------------------------------------------------------

	/**
	 * Process Kasper handler
	 * 
	 * @see com.viadeo.kasper.cqrs.command.CommandHandler
	 * @see AnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void process(final Class<?> handlerClazz) {
		LOGGER.info("Record on handler library : " + handlerClazz.getName());
		
		//- Register the handler to the locator -------------------------------
		getKasperLibrary().recordHandler((Class<? extends CommandHandler<?>>) handlerClazz);
	}

}

