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
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;
import com.viadeo.kasper.doc.KasperLibrary;

public final class DocumentedCommand extends AbstractDocumentedDomainNode {
	private static final long serialVersionUID = -4593630507564176805L;

	public static final String TYPE_NAME = "command";
	public static final String PLURAL_TYPE_NAME = "commands";

	private DocumentedProperties properties = null;

	// ------------------------------------------------------------------------

	DocumentedCommand(final KasperLibrary kl) { // Used as empty command to
												// populate
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);
	}

	public DocumentedCommand(final KasperLibrary kl, final Class<? extends ICommand> commandClazz) {
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);

		final XKasperCommand annotation = commandClazz
				.getAnnotation(XKasperCommand.class);
		
		// FIXME: domain removed from annotation
		/* final Class<? extends IDomain> domain = annotation.domain();
		final String domainName = domain.getSimpleName();
		*/
		final String domainName = "undocumented";

		// Get description ----------------------------------------------------
		String description = annotation.description();
		if (description.isEmpty()) {
			description = String.format("The %s command", 
					commandClazz.getSimpleName().replaceAll("Command", ""));
		}

		// - Register the domain to the locator --------------------------------
		this.setName(commandClazz.getSimpleName());
		this.setDomainName(domainName);
		this.setDescription(description);
		this.properties = new DocumentedProperties(commandClazz);
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
		final Optional<DocumentedHandler> handler = kl.getHandlerForCommand(
				getDomainName(), getName());

		if (handler.isPresent()) {
			return kl.getSimpleNodeFrom(handler.get());
		}

		return null;
	}

	// ------------------------------------------------------------------------

	public DocumentedProperties getProperties() {
		return this.properties;
	}

}
