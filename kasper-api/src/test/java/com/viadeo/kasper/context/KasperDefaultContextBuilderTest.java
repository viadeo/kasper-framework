package com.viadeo.kasper.context;

import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import junit.framework.TestCase;
import org.junit.Test;

public class KasperDefaultContextBuilderTest extends TestCase {

	@Test
	public void testDeterministicId() {
		final DefaultContextBuilder builder = new DefaultContextBuilder();
		final Context context = builder.buildDefault();
		assertEquals(context.getUserId(), context.getUserId());
	}
	
}
