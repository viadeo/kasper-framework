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
import com.viadeo.kasper.doc.KasperLibrary;

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
		final Class<? extends Command> commandClazz =
                commandHandlerResolver.getCommandClass(handlerClazz);

		// Find associated domain ---------------------------------------------
        final String domainName =
                commandHandlerResolver.getDomainClass(handlerClazz).get().getSimpleName();

		// Get description ----------------------------------------------------
		final String description = commandHandlerResolver.getDescription(handlerClazz);
        final String label = commandHandlerResolver.getLabel(handlerClazz);

		// Set properties -----------------------------------------------------
		this.commandName = commandClazz.getSimpleName();
		this.setName(handlerClazz.getSimpleName());
        this.setLabel(label);
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
