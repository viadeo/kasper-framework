// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition;

import com.viadeo.kasper.query.exposition.query.QueryBuilder;
import com.viadeo.kasper.query.exposition.query.QueryParser;

public class MyTestAdapter implements TypeAdapter<MyTestAdapter.MyTestAnswer> {

    static class MyTestAnswer {}

	@Override
	public void adapt(final MyTestAnswer value, final QueryBuilder builder) { }

	@Override
	public MyTestAnswer adapt(final QueryParser parser) { return null; }

}
