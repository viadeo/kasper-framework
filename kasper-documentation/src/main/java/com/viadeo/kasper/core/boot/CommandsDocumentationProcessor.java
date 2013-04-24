// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.viadeo.kasper.cqrs.command.ICommand;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;

/**
 * Process Kasper command dynamic registration at kasper boot
 *
 * @see XKasperCommand
 */
public class CommandsDocumentationProcessor extends AbstractDocumentationProcessor<XKasperCommand, ICommand> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommandsDocumentationProcessor.class);

	// ------------------------------------------------------------------------

	/**
	 * Process Kasper command
	 * 
	 * @see ICommand
	 * @see com.viadeo.kasper.core.boot.IAnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void process(final Class<?> commandClazz) {
		LOGGER.info("Record on command library : " + commandClazz.getName());
		
		//- Register the domain to the locator --------------------------------
		getKasperLibrary().recordCommand((Class<? extends ICommand>) commandClazz);
	}

	
}

