// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.exposition.query;

import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.common.exposition.TypeAdapter;

public interface QueryFactory {

	<T> TypeAdapter<T> create(TypeToken<T> typeToken);

}
