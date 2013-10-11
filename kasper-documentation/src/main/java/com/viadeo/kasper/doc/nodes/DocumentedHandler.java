// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.viadeo.kasper.core.resolvers.CommandHandlerResolver;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.exception.KasperException;

public final class DocumentedHandler extends DocumentedDomainNode {
	private static final long serialVersionUID = 2245288475776783642L;
	
	public static final String TYPE_NAME = "handler";
	public static final String PLURAL_TYPE_NAME = "handlers";
	
	private final String commandName;
	
	// ------------------------------------------------------------------------
	
	public DocumentedHandler(final KasperLibrary kl, final Class<? extends CommandHandler> handlerClazz) {
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);

        final CommandHandlerResolver commandHandlerResolver =
                this.getKasperLibrary().getResolverFactory().getCommandHandlerResolver();

		// Extract command type from handler ----------------------------------
		final Optional<Class<? extends Command>> commandClazz =
                commandHandlerResolver.getCommandClass(handlerClazz);

		// Find associated domain ---------------------------------------------
        String domainName = "[Not Resolved]";
        final Optional<String> optDomainName =
                commandHandlerResolver.getDomainLabel(handlerClazz);
        if (optDomainName.isPresent()) {
            domainName = optDomainName.get();
        }
		
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
		this.getKasperLibrary().registerHandler(this, this.commandName);
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
