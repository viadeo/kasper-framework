// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.common.db;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

import java.util.Map;

/*
 * Simulation of a key value store in-memory
 */
public class KeyValueStore {

    private final Map<Object, Object> keyValues = Maps.newHashMap();

    public void set(final Object key, final Object value) {
        keyValues.put(key, value);
    }

    public boolean has(final Object key) {
        return keyValues.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final Object key) {
        return Optional.fromNullable((T) keyValues.get(key));
    }

    public void del(final Object key) {
        keyValues.remove(key);
    }

}
