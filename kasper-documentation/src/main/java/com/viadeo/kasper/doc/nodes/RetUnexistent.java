// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

public class RetUnexistent extends RetError {
	private static final long serialVersionUID = -9151268172766961974L;
	
	final private String name;
	
	// ------------------------------------------------------------------------
	
	protected RetUnexistent() {
		name = "unknown";
	}
	
	public RetUnexistent(final String type, final String name) {
		super("Unexistent entity");		
		this.name = String.format("%s (%s)", name, type);
	}
	
	// ------------------------------------------------------------------------
	
	public String getName() {
		return this.name;
	}
	
}
