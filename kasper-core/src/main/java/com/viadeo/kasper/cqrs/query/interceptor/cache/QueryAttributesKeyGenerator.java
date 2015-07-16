// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.interceptor.cache;

import com.google.common.base.*;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.query.exposition.query.VisibilityFilter;
import org.reflections.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class QueryAttributesKeyGenerator<Q extends Query> implements QueryCacheKeyGenerator<Q> {

    private final ConcurrentHashMap<FieldCacheKey, Set<Field>> cache = new ConcurrentHashMap<>();

    private static final Comparator<Field> FIELD_BY_NAME_COMPARATOR = new Comparator<Field>() {
        @Override
        public int compare(final Field o1, final Field o2) {
        return o1.getName().compareTo(o2.getName());
        }
    };

    // ------------------------------------------------------------------------

    private static final class FieldCacheKey {

        private Class<? extends Query> queryClass;
        private String[] keys;

        private FieldCacheKey(final Class<? extends Query> queryClass, final String[] keys) {
            this.queryClass = checkNotNull(queryClass);
            this.keys = checkNotNull(keys);
        }

        @Override
        public boolean equals(final Object o) {

            if (this == checkNotNull(o)) {
                return true;
            }

            if ( ! getClass().equals(o.getClass())) {
                return false;
            }

            final FieldCacheKey that = (FieldCacheKey) o;

            if ( ! Arrays.equals(keys, that.keys)) {
                return false;
            }

            if ((null != queryClass) ?
                    ( ! queryClass.equals(that.queryClass)) :
                    (null != that.queryClass)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            final int response = queryClass != null ? queryClass.hashCode() : 0;
            return 31 * response + (keys != null ? Arrays.hashCode(keys) : 0);
        }

    }

    // ------------------------------------------------------------------------

    @Override
    public Serializable computeKey(final Optional<ID> user, final Q query, final String... fields) {
        final int queryHashCode;

        if (0 == fields.length) {
            queryHashCode = query.hashCode();
        } else {
            queryHashCode = Objects.hashCode(collectValues(query, collectFields(query.getClass(), fields)));
        }

        if (user.isPresent()) {
            return String.format("%s_%s_%s", query.getClass().getSimpleName(), user.get().getIdentifier(), queryHashCode);
        } else {
            return String.format("%s_%s", query.getClass().getSimpleName(), queryHashCode);
        }
    }

    Object[] collectValues(final Query query, final Set<Field> fields) {
        final Object[] values = new Object[fields.size()];

        int i = 0;
        for (final Field field : fields) {
            try {
                values[i++] = field.get(query);
            } catch (final IllegalAccessException e) {
                throw new KasperException("Error while building cache key for query " + query.getClass(), e);
            }
        }

        return values;
    }

    Set<Field> collectFields(final Class<? extends Query> queryClass, final String... keys) {
        final FieldCacheKey cachedKey = new FieldCacheKey(queryClass, keys);
        Set<Field> fields = cache.get(cachedKey);

        if (null == fields) {
            final Set<String> fieldNames = Sets.newHashSet(keys);

            final Predicate<Field> filteredByKeyAndModifier = new Predicate<Field>() {
                @Override
                public boolean apply(final Field field) {
                    return VisibilityFilter.DEFAULT.isVisible(field) && fieldNames.contains(field.getName());
                }
            };

            fields = new TreeSet<>(FIELD_BY_NAME_COMPARATOR);
            fields.addAll(ReflectionUtils.getAllFields(queryClass, filteredByKeyAndModifier));

            if (fields.size() != keys.length) {
                throw new IllegalStateException(
                        String.format(
                                "Could not find expected fields [%s] in query %s",
                                Joiner.on(',').join(retainMissingNames(fieldNames, fields)),
                                queryClass
                        )
                );
            }

            for (final Field field : fields) {
                if ( ! field.isAccessible()) {
                    field.setAccessible(true);
                }
            }

            cache.putIfAbsent(cachedKey, fields);
        }

        return fields;
    }

    final Set<String> retainMissingNames(final Set<String> expectedFieldNames, final Set<Field> discoveredFields) {

        final Set<String> discoveredNames = Sets.newHashSet(
                Iterables.transform(
                        discoveredFields,
                        new Function<Field, String>() {
                            @Override
                            public String apply(Field input) {
                                return input.getName();
                            }
                        }
                )
        );

        return Sets.filter(
                expectedFieldNames,
                new Predicate<String>() {
                    @Override
                    public boolean apply(final String input) {
                        return ! discoveredNames.contains(input);
                    }
                }
        );
    }

}
