// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context.impl;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import junit.framework.TestCase;
import org.junit.Test;

public class KasperDefaultContextBuilderTest extends TestCase {

	@Test
	public void testDeterministicId() {
        // Given
		final DefaultContextBuilder builder = new DefaultContextBuilder();

        // When
		final Context context = builder.build();

        // Then
		assertEquals(context.getUserId(), context.getUserId());
	}

    @Test
    public void testChild() {
        // Given
        final DefaultContext context = new DefaultContext();

        // When
        final DefaultContext newContext = (DefaultContext) context.child();

        // When
        assertEquals(context.getRequestCorrelationId(), newContext.getRequestCorrelationId());
        assertEquals(context.getSessionCorrelationId(), newContext.getSessionCorrelationId());
        assertEquals(context.getUserId(), newContext.getUserId());
        assertEquals(context.getUserLang(), newContext.getUserLang());
        assertEquals(context.getKasperCorrelationId(), newContext.getKasperCorrelationId());
        assertEquals(context.getProperties().size(), newContext.getProperties().size());
        assertEquals(context.getSequenceIncrement() + 1, newContext.getSequenceIncrement());
    }
	
}
