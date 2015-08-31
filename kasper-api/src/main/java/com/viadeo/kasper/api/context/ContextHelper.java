// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.context;

import java.util.Map;

/**
 * Implemented by objects that define a Context.
 */
public interface ContextHelper {
    Context createFrom(final Map<String, String> contextAsMap);
}
