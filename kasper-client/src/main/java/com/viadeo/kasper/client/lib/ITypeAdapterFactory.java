// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client.lib;

import com.google.common.base.Optional;

public interface ITypeAdapterFactory {
    
    <T> Optional<TypeAdapter<T>> create(TypeToken<T> typeToken, IQueryFactory adapterFactory);
    
}
