// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api;

public interface Format {

    /**
     * @param identifier the identifier
     * @return true if the identifier match this format, false otherwise
     */
    boolean accept(Object identifier);

    /**
     * @return the identifier type supported by this format
     */
    Class<?> identifierType();

    /**
     * @param <E> the type of the identifier
     * @return the parsed value according to the <code>Format</code> of the specified identifier.
     */
    <E> E parseIdentifier(String identifier);

    /**
     * @return the name of this format
     */
    String name();

    @Override
    String toString();
}
