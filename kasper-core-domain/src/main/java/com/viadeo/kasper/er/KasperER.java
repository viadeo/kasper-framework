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
public class KasperER {

	/**
	 * SOURCE is the child of TARGET
	 */
	static final public String child_of = "child_of";
	
	/**
	 * SOURCE has been created by TARGET
	 */
	static final public String created_by = "created_by";
	
}
