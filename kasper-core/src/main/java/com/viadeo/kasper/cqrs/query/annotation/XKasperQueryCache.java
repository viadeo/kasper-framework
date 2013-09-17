package com.viadeo.kasper.cqrs.query.annotation;

import com.viadeo.kasper.cqrs.query.QueryCacheKeyGenerator;
import com.viadeo.kasper.cqrs.query.impl.QueryAttributesKeyGenerator;

import java.lang.annotation.*;

/**
 * This annotation can be used in conjunction with @XKasperQueryService to enable a cache for the corresponding QueryService.
 * By default the cache is disabled.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface XKasperQueryCache {
    /**
     * @return true if the cache should be enabled for this QueryService. True by default.
     */
    boolean value() default true;

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
    // FIXME name it keys or fields? This corresponds to the set of fields to be used in computing the key
    String[] keys() default {};
}
