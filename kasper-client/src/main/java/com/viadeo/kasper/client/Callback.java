// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

/**
 * A callback class used in {@link com.viadeo.kasper.client.KasperClient} for async commands/queries.
 * 
 * @param <T> the response type parameter
 * @see com.viadeo.kasper.client.KasperClient
 */
public interface Callback<T> {

    /**
     * Called when further operation has been done.
     * 
     * @param response of the operation.
     */
    void done(T response);

}
