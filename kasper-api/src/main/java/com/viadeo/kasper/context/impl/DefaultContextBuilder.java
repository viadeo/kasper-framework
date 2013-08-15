// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context.impl;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.ContextBuilder;

/**
 *
 * Default context builder used as a last chance if no other implementation can be found
 * @see com.viadeo.kasper.context.ContextBuilder
 *
 */
public class DefaultContextBuilder implements ContextBuilder {

	@Override
	public Context build() {
		return new DefaultContext();
	}

    public static Context get() {
        return new DefaultContext();
    }

}
