// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.interceptor.cache;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.api.domain.query.Query;

import java.io.Serializable;

/**
 * This interface can be implemented by classes that want to implement a custom key generation strategy for query caches.
 */
public interface QueryCacheKeyGenerator<Q extends Query> {

    /**
     * @param user for which we want to compute the key
     * @param query for which we want to compute the key
     * @param fields of the given query for which we want to build the caches key.
     * @return the compute key for this entry, it will be used to store/retrieve QueryResult from the cache.
     */
    Serializable computeKey(Optional<ID> user, Q query, String... fields);

}
