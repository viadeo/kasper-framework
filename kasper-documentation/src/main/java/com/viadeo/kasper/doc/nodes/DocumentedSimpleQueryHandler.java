// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

public class DocumentedSimpleQueryHandler extends DocumentedNode {
	private static final long serialVersionUID = 280370243066985843L;

	private String queryName = "unknown";
	private String resultName = "unknown";

    // ------------------------------------------------------------------------

	public DocumentedSimpleQueryHandler(final DocumentedQueryHandler handler) {
		super(handler);

		this.queryName = handler.getQueryName();
		this.resultName = handler.getQueryResultName();
	}

	// --

	public String getQueryName() {
		return this.queryName;
	}
	
	// --
	
	public String getResultName() {
		return this.resultName;
	}	
	
}
