// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client;

/**
 * @param <T> the result type parameter
 */
public interface ICallback<T> {
    
    /**
     * Called when further operation is done
     * 
     * @param result the result to be processed
     */
    void done(T result);
    
}
