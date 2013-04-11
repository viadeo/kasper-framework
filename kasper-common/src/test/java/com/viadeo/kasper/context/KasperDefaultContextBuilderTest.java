package com.viadeo.kasper.context;

import junit.framework.TestCase;

import com.viadeo.kasper.context.IContext;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;

public class KasperDefaultContextBuilderTest extends TestCase {

	public void testDeterministicId() {
		final DefaultContextBuilder builder = new DefaultContextBuilder();
		final IContext context = builder.buildDefault();
		assertEquals(context.getUserId(), context.getUserId());
	}
	
}
