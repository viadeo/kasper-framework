// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.id;

/**
 * Implemented by objects that can be able to build an <code>ID</code> from an URN.
 */
public interface IDBuilder {

    /**
     *
     * @param urn a chain of chars representing an URN
     * @return a corresponding id from the specified URN
     */
    ID build(String urn);
}
