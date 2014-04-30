// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.strategy;

import com.viadeo.kasper.context.Context;

/**
 * Defines a security strategy that applies to a given (query or command) handler instance.
 * A security strategy is defined by actions that are taken before the request is handled by its handler and actions
 * that are taken after it has been handled.
 * Typical actions consist in calling a few callbacks (representing security capabilities like verifying the validity
 * of a security token, for example) in an order defined by the strategy itself.
 */
public interface SecurityStrategy {

    void beforeRequest(final Context context);

    void afterRequest();

}
