// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.context;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * UnitOfWork decorator to handle context
 *
 */
public final class CurrentContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentContext.class);

	private CurrentContext() { /* singleton */ }
	
	private static final ThreadLocal <Context> CONTEXT = new ThreadLocal<>();
	
	// ------------------------------------------------------------------------
	
	public static Optional<Context> value() {
        return Optional.fromNullable(CONTEXT.get());
	}
	
	// ------------------------------------------------------------------------
	
	public static void set(final Context context) {
        LOGGER.debug("Set current thread context {}", checkNotNull(context));
		CONTEXT.set(context);
	}
	
}
