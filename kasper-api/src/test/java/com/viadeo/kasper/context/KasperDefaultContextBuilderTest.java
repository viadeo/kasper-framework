// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context;

import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import junit.framework.TestCase;
import org.junit.Test;

public class KasperDefaultContextBuilderTest extends TestCase {

	@Test
	public void testDeterministicId() {
		final DefaultContextBuilder builder = new DefaultContextBuilder();
		final Context context = builder.build();
		assertEquals(context.getUserId(), context.getUserId());
	}
	
}
