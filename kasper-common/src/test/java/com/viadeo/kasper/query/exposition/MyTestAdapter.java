// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition;

public class MyTestAdapter implements ITypeAdapter<Integer>{

	@Override
	public void adapt(final Integer value, final QueryBuilder builder) { }

	@Override
	public Integer adapt(final QueryParser parser) { return null; }

}
