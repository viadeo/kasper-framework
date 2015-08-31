// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.id;

import org.junit.Test;

import static org.junit.Assert.*;

public class AbstractKasperIDTest {

	private static final int INT_ID_A = 42;
	private static final int INT_ID_B = 24;

    // ------------------------------------------------------------------------
	
	private class TestID extends AbstractKasperID<Integer> {
		private static final long serialVersionUID = -1463378777340827163L;
		
		TestID(final Integer value) {
			super(value);
		}
	}
	
	// ------------------------------------------------------------------------
	
	@Test
	public void testID() {
		final TestID id1 = new TestID(INT_ID_A);
		final TestID id2 = new TestID(INT_ID_A);
		final TestID id3 = new TestID(INT_ID_B);
		
		assertTrue(id1.equals(id1));
		assertTrue(id1.equals(id2));
		assertTrue(id1.equals(INT_ID_A));
		
		assertFalse(id1.equals(id3));
		assertFalse(id1.equals(INT_ID_B));
	}	
	
	// ------------------------------------------------------------------------
	
	@Test
	public void testHashCode() {
		final TestID id1 = new TestID(INT_ID_A);
		final TestID id2 = new TestID(INT_ID_A);
		final TestID id3 = new TestID(INT_ID_B);
		
		assertEquals(id1.hashCode(), id1.hashCode());
		assertEquals(id1.hashCode(), id2.hashCode());
		assertNotSame(id1.hashCode(), id3.hashCode());		
	}
	
}
