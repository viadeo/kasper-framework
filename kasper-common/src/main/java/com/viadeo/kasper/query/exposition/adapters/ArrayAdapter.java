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

import java.lang.reflect.Array;

import static java.lang.System.arraycopy;

public final class ArrayAdapter implements TypeAdapter<Object> {
    private final TypeAdapter<Object> componentAdapter;
    private final Class componentClass;

    // ------------------------------------------------------------------------

    public ArrayAdapter(final TypeAdapter componentAdapter, final Class componentClass) {
        this.componentAdapter = componentAdapter;
        this.componentClass = componentClass;
    }

    // ------------------------------------------------------------------------

    @Override
    public void adapt(final Object array, final QueryBuilder builder) throws Exception {
        final int len = Array.getLength(array);

        for (int i = 0; i < len; i++) {
            final Object element = Array.get(array, i);
            componentAdapter.adapt(element, builder);
        }
    }

    @Override
    public Object adapt(final QueryParser parser) throws Exception {
        int size = DefaultTypeAdapters.PARSER_ARRAY_STARTING_SIZE;
        Object array = Array.newInstance(componentClass, size);
        int idx = 0;

        for (final QueryParser nextParser : parser) {
            if (idx >= size) {
                size = size * 2 + 1;
                array = expandArray(array, idx, size);
            }
            Array.set(array, idx++, componentAdapter.adapt(nextParser));
        }
        if (idx < size) {
            array = expandArray(array, idx, idx);
        }
        return array;
    }

    private Object expandArray(final Object array, final int len, final int size) {
        final Object tmpArray = Array.newInstance(componentClass, size);
        arraycopy(array, 0, tmpArray, 0, len);
        return tmpArray;
    }

}
