// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client.lib;

public abstract class TypeAdapter<T> {
    
    public abstract void adapt(T value, QueryBuilder builder);

    public final TypeAdapter<T> skipNull() {
        return new TypeAdapter<T>() {
            @Override
            public void adapt(final T value, final QueryBuilder builder) {
                if (null != value) {
                    TypeAdapter.this.adapt(value, builder);
                }
            }
        };
    }
    
}
