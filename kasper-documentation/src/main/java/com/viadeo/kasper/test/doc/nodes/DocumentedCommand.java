// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.doc.nodes;

import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;
import com.viadeo.kasper.test.doc.KasperLibrary;

public final class DocumentedCommand extends DocumentedDomainNode {
	private static final long serialVersionUID = -4593630507564176805L;

	public static final String TYPE_NAME = "command";
	public static final String PLURAL_TYPE_NAME = "commands";

	private DocumentedBean properties = null;

	// ------------------------------------------------------------------------

	DocumentedCommand(final KasperLibrary kl) { // Used as empty command to
												// populate
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);
	}

	public DocumentedCommand(final KasperLibrary kl, final Class<? extends Command> commandClazz) {
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);

		final XKasperCommand annotation = commandClazz
				.getAnnotation(XKasperCommand.class);

		// Get description ----------------------------------------------------
		String description = annotation.description();
		if (description.isEmpty()) {
			description = String.format("The %s command", 
					commandClazz.getSimpleName().replaceAll("Command", ""));
		}

		// - Register the domain to the locator --------------------------------
		this.setName(commandClazz.getSimpleName());
		this.setDescription(description);
		this.properties = new DocumentedBean(commandClazz);
	}

	// ------------------------------------------------------------------------

	public String getLabel() {
		if (null == this.label) {
			return this.getName().replaceAll("Command", "");
		}
		return super.getLabel();
	}

	// ------------------------------------------------------------------------

	public DocumentedNode getHandler() {
		final KasperLibrary kl = this.getKasperLibrary();
		final Optional<DocumentedHandler> handler = kl.getHandlerForCommand(getName());

		if (handler.isPresent()) {
			return kl.getSimpleNodeFrom(handler.get());
		}

		return null;
	}

	// ------------------------------------------------------------------------

	public DocumentedBean getProperties() {
		return this.properties;
	}
	
	// ------------------------------------------------------------------------
	
	public DocumentedNode getDomain() {
		final Optional<DocumentedHandler> handler = this.getKasperLibrary().getHandlerForCommand(this.getName());
		if (handler.isPresent()) {
			return new DocumentedNode(handler.get().getDomain());
		}
		return null;
	}	

}
