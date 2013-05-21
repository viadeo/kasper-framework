// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er;

/**
 * 
 * Convenient class used to store common relation verbs and other E/R helpers
 * 
 */
public final class KasperER {

	private KasperER() { /* singleton */ }
	
	/**
	 * SOURCE is the child of TARGET
	 */
	public static final String CHILD_OF = "child_of";
	
	/**
	 * SOURCE has been created by TARGET
	 */
	public static final String CREATED_BY = "created_by";
	
}
