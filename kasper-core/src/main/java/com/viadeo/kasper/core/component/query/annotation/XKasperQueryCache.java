// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query.annotation;

import com.viadeo.kasper.core.component.query.interceptor.cache.QueryAttributesKeyGenerator;
import com.viadeo.kasper.core.component.query.interceptor.cache.QueryCacheKeyGenerator;

import java.lang.annotation.*;

/**
 * This annotation can be used in conjunction with @XKasperQueryHandler to enable a cache for the corresponding QueryHandler.
 * By default the cache is disabled.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface XKasperQueryCache {

    /**
     * @return true if the cache should be enabled for this QueryHandler. True by default.
     */
    boolean enabled() default true;

    /**
     * @return The duration in seconds instances of that cache should live (they can still by evicted if other entries are preferred).
     *         Default to one hour.
     */
    long ttl() default 3600;

    /**
     * @return The key generator to use for this cache. By default QueryAttributesKeyGenerator is used.
     */
    Class<? extends QueryCacheKeyGenerator> keyGenerator() default QueryAttributesKeyGenerator.class;

    /**
     * @return The fields to use for building this caches key.
     */
    String[] keys() default {};

}
