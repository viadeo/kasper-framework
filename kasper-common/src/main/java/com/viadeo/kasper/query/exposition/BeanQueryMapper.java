// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition;

import com.google.common.collect.ImmutableSet;
import com.viadeo.kasper.cqrs.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class BeanQueryMapper implements TypeAdapter<Query> {
    private final Set<PropertyAdapter> adapters;
    private final BeanConstructor queryCtr;

    public BeanQueryMapper(final BeanConstructor queryCtr, final Set<PropertyAdapter> adapters) {
        this.adapters = ImmutableSet.copyOf(adapters);
        this.queryCtr = queryCtr;
    }

    @Override
    public void adapt(final Query value, final QueryBuilder builder) throws Exception {
        for (final PropertyAdapter adapter : adapters) {
            adapter.adapt(value, builder);
        }
    }

    @Override
    public Query adapt(final QueryParser parser) throws Exception {
        final Object[] ctrParams = new Object[queryCtr.parameters().size()];
        final List<DefaultQueryFactory.Pair<PropertyAdapter, Object>> valuesToSet = new ArrayList<DefaultQueryFactory.Pair<PropertyAdapter, Object>>();

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

            if (ctrParam != null) {
                ctrParams[ctrParam.position()] = value;
            } else {
                if (exists) {
                    valuesToSet.add(new DefaultQueryFactory.Pair<PropertyAdapter, Object>(adapter, value));
                }
            }
        }

        final Object queryInstance = queryCtr.create(ctrParams);
        for (final DefaultQueryFactory.Pair<PropertyAdapter, Object> pair : valuesToSet) {
            pair.firstValue().mutate(queryInstance, pair.secondValue());
        }

        return (Query) queryInstance;
    }
}
