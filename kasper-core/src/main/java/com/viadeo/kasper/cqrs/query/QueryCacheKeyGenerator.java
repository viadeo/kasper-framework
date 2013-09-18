package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryCache;

import java.io.Serializable;

/**
 * This interface can be implemented by classes that want to implement a custom key generation strategy for query caches.
 */
public interface QueryCacheKeyGenerator<Q extends Query> {
    /**
     * @param cache for which this key generator is used (implementations can this way get some information from the cache configuration)
     * @param query for which we want to compute the key
     * @return the compute key for this entry, it will be used to store/retrieve QueryPayload from the cache.
     */
    public Serializable computeKey(XKasperQueryCache cache, Q query);
}
