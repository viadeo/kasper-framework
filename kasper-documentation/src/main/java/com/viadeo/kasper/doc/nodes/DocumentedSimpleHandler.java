// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

public class DocumentedSimpleHandler extends DocumentedNode {
	private static final long serialVersionUID = 280370243066985843L;

	private String commandName = "unknown";

    // ------------------------------------------------------------------------

	public DocumentedSimpleHandler(final DocumentedHandler handler) {
		super(handler);

		this.commandName = handler.getCommandName();
	}

	// --

	public String getCommandName() {
		return this.commandName;
	}
	
}
