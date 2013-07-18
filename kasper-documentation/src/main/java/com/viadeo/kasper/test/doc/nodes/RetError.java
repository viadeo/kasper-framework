// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.doc.nodes;


public class RetError extends RetBase {
	private static final long serialVersionUID = 8985310989912862924L;

	private final String message;
	
	private static final String TYPE = "error";
	
	// ------------------------------------------------------------------------
	
	protected RetError() {
		super(TYPE);
		this.message = "unknown";
	}
	
	public RetError(final String message) {
		super(TYPE);		
		this.message = message;
	}

	// ------------------------------------------------------------------------
	
	public String getMessage() {
		return this.message;
	}
	
}
