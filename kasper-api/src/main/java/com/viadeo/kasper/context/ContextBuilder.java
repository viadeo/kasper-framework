// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context;

/**
 *
 * Builds a default execution context when no one has been submitted
 *
 * @see Context
 *
 */
public interface ContextBuilder {

	/**
	 * @return a default context
	 */
	Context build();
	
}
