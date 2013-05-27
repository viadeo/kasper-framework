package com.viadeo.kasper.query.exposition;

import org.junit.Test;
import static org.junit.Assert.*;

public class QueryFactoryBuilderTest {
	@Test
	public void testTypeAdapterServiceLoading() {
		for (ITypeAdapter<?> adapter : new QueryFactoryBuilder()
				.loadDeclaredAdapters())
			if (MyTestAdapter.class.equals(adapter.getClass()))
				return;
		fail();
	}
}
