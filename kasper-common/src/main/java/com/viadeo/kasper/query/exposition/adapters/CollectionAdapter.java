// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition.adapters;

import com.viadeo.kasper.query.exposition.TypeAdapter;
import com.viadeo.kasper.query.exposition.query.QueryBuilder;
import com.viadeo.kasper.query.exposition.query.QueryParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class CollectionAdapter<E> implements TypeAdapter<Collection<E>> {

    private final TypeAdapter<E> elementAdapter;

    // ------------------------------------------------------------------------

    CollectionAdapter(final TypeAdapter<E> elementAdapter) {
        this.elementAdapter = elementAdapter;
    }

    // ------------------------------------------------------------------------

    @Override
    public void adapt(final Collection<E> value, final QueryBuilder builder) throws Exception {
        for (final E element : value) {
            elementAdapter.adapt(element, builder);
        }
    }

    public Collection<E> adapt(final QueryParser parser) throws Exception {
        final List<E> listOfE = new ArrayList<E>();
        for (final QueryParser next : parser) {
            listOfE.add(elementAdapter.adapt(next));
        }
        return listOfE;
    }

}
