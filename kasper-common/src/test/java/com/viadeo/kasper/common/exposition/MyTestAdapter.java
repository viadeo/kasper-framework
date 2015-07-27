// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.exposition;

import com.viadeo.kasper.common.exposition.query.QueryBuilder;
import com.viadeo.kasper.common.exposition.query.QueryParser;

public class MyTestAdapter implements TypeAdapter<MyTestAdapter.MyTestResult> {

    static class MyTestResult {}

	@Override
	public void adapt(final MyTestResult value, final QueryBuilder builder) { }

	@Override
	public MyTestResult adapt(final QueryParser parser) { return null; }

}
