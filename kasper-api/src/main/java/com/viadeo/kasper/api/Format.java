// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api;

/**
 * Implemented by objects that define a format.
 */
public interface Format {

    /**
     * @param identifier the identifier
     * @return true if the identifier match this format, false otherwise
     */
    boolean accept(Object identifier);

    /**
     * Returns the type of the identifier that this format support.
     *
     * @return the identifier type supported by this format
     */
    Class<?> identifierType();

    /**
     * Returns the parsed identifier.
     *
     * @param identifier the identifier
     * @param <E> the type of the identifier
     * @return the parsed value according to the <code>Format</code> of the specified identifier.
     */
    <E> E parseIdentifier(String identifier);

    /**
     * Returns the name of this format.
     *
     * @return the name of this format
     */
    String name();

    /**
     * @return an URN representation
     */
    @Override
    String toString();
}
