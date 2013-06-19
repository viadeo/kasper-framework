// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Process Kasper command dynamic registration at platform boot
 *
 * @see XKasperCommand
 */
public class CommandsDocumentationProcessor extends DocumentationProcessor<XKasperCommand, Command> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommandsDocumentationProcessor.class);

	// ------------------------------------------------------------------------

	/**
	 * Process Kasper command
	 * 
	 * @see com.viadeo.kasper.cqrs.command.Command
	 * @see AnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void process(final Class<?> commandClazz) {
		LOGGER.info("Record on command library : " + commandClazz.getName());
		
		//- Register the domain to the locator --------------------------------
		getKasperLibrary().recordCommand((Class<? extends Command>) commandClazz);
	}

	
}

