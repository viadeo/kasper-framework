// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.google.common.base.Optional;
import com.viadeo.kasper.IDomain;
import com.viadeo.kasper.cqrs.command.ICommand;
import com.viadeo.kasper.cqrs.command.ICommandHandler;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.exception.KasperRuntimeException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;


public final class DocumentedHandler extends AbstractDocumentedDomainNode {
	private static final long serialVersionUID = 2245288475776783601L;
	
	static public final String TYPE_NAME = "handler";
	static public final String PLURAL_TYPE_NAME = "handlers";
	
	private final String commandName;
	
	// ------------------------------------------------------------------------
	
	public DocumentedHandler(final KasperLibrary kl, final Class<? extends ICommandHandler<?>> handlerClazz) {
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);
		
		// Extract event type from handler -----------------------------------
		@SuppressWarnings("unchecked") // Safe
		final Optional<Class<? extends ICommand>> commandClazz =  
				(Optional<Class<? extends ICommand>>) 
					ReflectionGenericsResolver.getParameterTypeFromClass(
						handlerClazz, ICommandHandler.class, ICommandHandler.COMMAND_PARAMETER_POSITION);
		
		if (!commandClazz.isPresent()) {
			throw new KasperRuntimeException("Unable to find command type for handler " + handlerClazz.getClass());
		}
		
		// Find associated domain ---------------------------------------------		
		final XKasperCommand commandAnno = commandClazz.get().getAnnotation(XKasperCommand.class);
		final Class<? extends IDomain> domain = commandAnno.domain();	
		final String domainName = domain.getSimpleName();
		
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
		final Optional<DocumentedCommand> concept = kl.getCommand(this.getDomainName(), this.commandName);
		
		if (concept.isPresent()) {
			return kl.getSimpleNodeFrom( concept.get() ); 
		}
		
		return new DocumentedCommand(getKasperLibrary())
			.setDomainName(getDomainName())
			.setName(this.commandName)
			.setDescription("[Not resolved]")
			.toSimpleNode();
	}
	
}
