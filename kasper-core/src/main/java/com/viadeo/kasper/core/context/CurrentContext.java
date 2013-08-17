// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.context;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.AbstractContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UnitOfWork decorator to handle context
 *
 */
public final class CurrentContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentContext.class);

	private CurrentContext() { /* singleton */ }
	
	private static final ThreadLocal <Context> CONTEXT = new ThreadLocal<Context>();
	
	// ------------------------------------------------------------------------
	
	public static Optional<Context> value() {
        return Optional.fromNullable(CONTEXT.get());
	}
	
	// ------------------------------------------------------------------------
	
	public static void set(final Context context) {
        LOGGER.debug("Set current thread context {}", context);

        // Sets the Kasper correlation id if it has not been set before -------
        if (AbstractContext.class.isAssignableFrom(context.getClass())) {
            final AbstractContext kasperContext = (AbstractContext) context;
            kasperContext.setValidKasperCorrelationId();
        }

		CONTEXT.set(context);
	}
	
}
