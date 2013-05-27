// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client.lib;

/**
 * A callback class used in {@link KasperClient} for async commands/queries.
 * 
 * @param <T> the result type parameter
 * @see com.viadeo.kasper.client.KasperClient KasperClient
 */
public interface ICallback<T> {

    /**
     * Called when further operation has been done.
     * 
     * @param result of the operation.
     */
    void done(T result);

}
