// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.exposition.adapters;

import com.viadeo.kasper.common.exposition.TypeAdapter;
import com.viadeo.kasper.common.exposition.query.QueryBuilder;
import com.viadeo.kasper.common.exposition.query.QueryParser;

public class EnumAdapter<T extends Enum<T>> implements TypeAdapter<T> {

    private final Class<T> eClass;

    // ------------------------------------------------------------------------

    public EnumAdapter(final Class<T> eClass) {
        this.eClass = eClass;
    }

    // ------------------------------------------------------------------------

    public void adapt(final T obj, final QueryBuilder builder) {
        builder.add(obj.name());
    }

    public T adapt(final QueryParser parser) {
        return Enum.valueOf(eClass, parser.value());
    }

}
