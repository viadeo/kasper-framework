// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition.query;

import com.google.common.collect.ImmutableSet;
import com.viadeo.kasper.api.domain.query.Query;
import com.viadeo.kasper.query.exposition.TypeAdapter;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

class BeanQueryMapper implements TypeAdapter<Query> {

    private final Set<PropertyAdapter> adapters;
    private final BeanConstructor queryCtr;

    // ------------------------------------------------------------------------

    public BeanQueryMapper(final BeanConstructor queryCtr, final Set<PropertyAdapter> adapters) {
        this.adapters = ImmutableSet.copyOf(sortPropertyAdapterSet(checkNotNull(adapters)));
        this.queryCtr = checkNotNull(queryCtr);
    }

    // ------------------------------------------------------------------------

    @Override
    public void adapt(final Query value, final QueryBuilder builder) throws Exception {
        for (final PropertyAdapter adapter : adapters) {
            adapter.adapt(value, builder);
        }
    }

    @Override
    public Query adapt(final QueryParser parser) throws Exception {
        final Object[] ctrParams = new Object[queryCtr.parameters().size()];
        final List<PropertyAdapterPair<PropertyAdapter, Object>> valuesToSet = new ArrayList<PropertyAdapterPair<PropertyAdapter, Object>>();

        for (final PropertyAdapter adapter : adapters) {
            /*
             * we have to check if the property exists in th sream if it
             * doesn't we should not override it in case of setters (for the
             * ctr we have no choice as we can't pass null to primitive
             * args)
             */
            final boolean exists = adapter.existsInQuery(parser);
            final Object value = adapter.adapt(parser);
            final BeanConstructorProperty ctrParam = queryCtr.parameters().get(adapter.getName());

            if (null != ctrParam) {
                ctrParams[ctrParam.position()] = value;
            } else {
                if (exists) {
                    valuesToSet.add(
                            new PropertyAdapterPair<PropertyAdapter, Object>(adapter, value)
                    );
                }
            }
        }

        final Object queryInstance = queryCtr.create(ctrParams);
        for (final PropertyAdapterPair<PropertyAdapter, Object> pair : valuesToSet) {
            pair.firstValue().mutate(queryInstance, pair.secondValue());
        }

        return (Query) queryInstance;
    }

    private SortedSet<PropertyAdapter> sortPropertyAdapterSet(final Set<PropertyAdapter> propertyAdapters) {
        final SortedSet<PropertyAdapter> sorted = new TreeSet<PropertyAdapter>(new Comparator<PropertyAdapter>() {
            @Override
            public int compare(PropertyAdapter o1, PropertyAdapter o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (PropertyAdapter propertyAdapter : propertyAdapters) {
            sorted.add(propertyAdapter);
        }
        return sorted;
    }

}
