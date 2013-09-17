package com.viadeo.kasper.cqrs.query.impl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryCacheKeyGenerator;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryCache;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.query.exposition.query.VisibilityFilter;
import org.reflections.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QueryAttributesKeyGenerator implements QueryCacheKeyGenerator {
    private final ConcurrentHashMap<FieldCacheKey, Set<Field>> cache = new ConcurrentHashMap<>();

    private final static Comparator<Field> FIELD_BY_NAME_COMPARATOR = new Comparator<Field>() {
        @Override
        public int compare(Field o1, Field o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    @Override
    public Serializable computeKey(XKasperQueryCache cache, Query query) {
        if (cache.keys().length == 0)
            return query.hashCode();
        else {
            return Objects.hashCode(collectValues(query, collectFields(query.getClass(), cache.keys())));
        }
    }

    Object[] collectValues(Query query, Set<Field> fields) {
        Object[] values = new Object[fields.size()];

        int i = 0;
        for (Field field : fields) {
            try {
                values[i++] = field.get(query);
            } catch (IllegalAccessException e) {
                throw new KasperException("Error while building cache key for query " + query.getClass(), e);
            }
        }

        return values;
    }

    Set<Field> collectFields(final Class<? extends Query> queryClass, final String... keys) {
        FieldCacheKey cachedKey = new FieldCacheKey(queryClass, keys);
        Set<Field> fields = cache.get(cachedKey);

        if (fields == null) {
            final Set<String> fieldNames = Sets.newHashSet(keys);

            final Predicate<Field> filteredByKeyAndModifier = new Predicate<Field>() {
                @Override
                public boolean apply(Field field) {
                    return VisibilityFilter.DEFAULT.isVisible(field) && fieldNames.contains(field.getName());
                }
            };

            fields = new TreeSet<>(FIELD_BY_NAME_COMPARATOR);
            fields.addAll(ReflectionUtils.getAllFields(queryClass, filteredByKeyAndModifier));

            if (fields.size() != keys.length)
                throw new IllegalStateException("Could not find expected fields [" + Joiner.on(',').join(retainMissingNames(fieldNames, fields)) + "] in query " + queryClass);

            for (Field field : fields) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
            }
            cache.putIfAbsent(cachedKey, fields);
        }

        return fields;
    }

    Set<String> retainMissingNames(final Set<String> expectedFieldNames, final Set<Field> discoveredFields) {
        final Set<String> discoveredNames = Sets.newHashSet(Iterables.transform(discoveredFields, new Function<Field, String>() {
            @Override
            public String apply(Field input) {
                return input.getName();
            }
        }));

        return Sets.filter(expectedFieldNames, new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return !discoveredNames.contains(input);
            }
        });
    }

    private class FieldCacheKey {

        private Class<? extends Query> queryClass;
        private String[] keys;

        private FieldCacheKey(Class<? extends Query> queryClass, String[] keys) {
            this.queryClass = queryClass;
            this.keys = keys;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FieldCacheKey that = (FieldCacheKey) o;

            if (!Arrays.equals(keys, that.keys)) return false;
            if (queryClass != null ? !queryClass.equals(that.queryClass) : that.queryClass != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = queryClass != null ? queryClass.hashCode() : 0;
            result = 31 * result + (keys != null ? Arrays.hashCode(keys) : 0);
            return result;
        }
    }
}
