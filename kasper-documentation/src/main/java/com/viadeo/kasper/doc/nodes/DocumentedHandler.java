// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

public final class DocumentedHandler extends DocumentedDomainNode {
	private static final long serialVersionUID = 2245288475776783642L;
	
	public static final String TYPE_NAME = "handler";
	public static final String PLURAL_TYPE_NAME = "handlers";
	
	private final String commandName;
	
	// ------------------------------------------------------------------------
	
	public DocumentedHandler(final KasperLibrary kl, final Class<? extends CommandHandler<?>> handlerClazz) {
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);
		
		// Extract command type from handler ----------------------------------
		@SuppressWarnings("unchecked") // Safe
		final Optional<Class<? extends Command>> commandClazz =
				(Optional<Class<? extends Command>>)
					ReflectionGenericsResolver.getParameterTypeFromClass(
						handlerClazz, CommandHandler.class, CommandHandler.COMMAND_PARAMETER_POSITION);
		
		if (!commandClazz.isPresent()) {
			throw new KasperException("Unable to find command type for handler " + handlerClazz.getClass());
		}
		
		// Find associated domain ---------------------------------------------		
		final XKasperCommandHandler handlerAnno = handlerClazz.getAnnotation(XKasperCommandHandler.class);
		final String domainName = handlerAnno.domain().getSimpleName();
		
		// Get description ----------------------------------------------------
		final XKasperCommandHandler annotation = handlerClazz.getAnnotation(XKasperCommandHandler.class);
		String description = annotation.description();
		if (description.isEmpty()) {
			description = String.format("The handler for %s commands", commandClazz.get().getSimpleName().replaceAll("Command", ""));
		}
		
		// Set properties -----------------------------------------------------
		this.commandName = commandClazz.get().getSimpleName();
		this.setName(handlerClazz.getSimpleName());
		this.setDescription(description);
		this.setDomainName(domainName);
	}	
	
	// ------------------------------------------------------------------------
	
	public DocumentedNode getCommand() {
		final KasperLibrary kl = this.getKasperLibrary();
		final Optional<DocumentedCommand> command = kl.getCommand(this.commandName);
		
		if (command.isPresent()) {
			return kl.getSimpleNodeFrom( command.get() ); 
		}
		
		return new DocumentedCommand(getKasperLibrary())
			.setDomainName(getDomainName())
			.setName(this.commandName)
			.setDescription("[Not resolved]")
			.toSimpleNode();
	}
	
	@JsonIgnore
	public String getCommandName(){
		return this.commandName;
	}

}
