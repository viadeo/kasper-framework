// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.query.exposition;

import com.google.common.reflect.TypeToken;

public interface IQueryFactory {

	<T> ITypeAdapter<T> create(TypeToken<T> typeToken);

}
