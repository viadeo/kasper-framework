// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.IContext;

/**
 * UnitOfWork decorator to handle context
 *
 */
public final class CurrentContext {

	private CurrentContext() { /* singleton */ }
	
	private static final ThreadLocal < IContext > CONTEXT = new ThreadLocal<IContext>();
	
	// ------------------------------------------------------------------------
	
	public static Optional<IContext> value() {        
        return Optional.fromNullable(CONTEXT.get());
	}
	
	// ------------------------------------------------------------------------
	
	public static void set(final IContext context) {
		CONTEXT.set(context);
	}
	
}
