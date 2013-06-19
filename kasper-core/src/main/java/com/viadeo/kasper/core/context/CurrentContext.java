// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.context;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.Context;

/**
 * UnitOfWork decorator to handle context
 *
 */
public final class CurrentContext {

	private CurrentContext() { /* singleton */ }
	
	private static final ThreadLocal <Context> CONTEXT = new ThreadLocal<Context>();
	
	// ------------------------------------------------------------------------
	
	public static Optional<Context> value() {
        return Optional.fromNullable(CONTEXT.get());
	}
	
	// ------------------------------------------------------------------------
	
	public static void set(final Context context) {
		CONTEXT.set(context);
	}
	
}