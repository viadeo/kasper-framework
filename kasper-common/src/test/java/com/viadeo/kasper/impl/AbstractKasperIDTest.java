package com.viadeo.kasper.impl;

import junit.framework.TestCase;

public class AbstractKasperIDTest extends TestCase {

	private class TestID extends AbstractKasperID<Integer> {
		private static final long serialVersionUID = -1463378777340827163L;
		
		TestID(final Integer value) {
			super(value);
		}
	}
	
	// ------------------------------------------------------------------------
	
	public void testID() {
		final TestID id1 = new TestID(42);
		final TestID id2 = new TestID(42);
		final TestID id3 = new TestID(43);
		
		assertTrue(id1.equals(id1));
		assertTrue(id1.equals(id2));
		assertTrue(id1.equals(42));
		
		assertFalse(id1.equals(id3));
		assertFalse(id1.equals(43));
	}	
	
	// ------------------------------------------------------------------------
	
	public void testHashCode() {
		final TestID id1 = new TestID(42);
		final TestID id2 = new TestID(42);
		final TestID id3 = new TestID(43);
		
		assertEquals(id1.hashCode(), id1.hashCode());
		assertEquals(id1.hashCode(), id2.hashCode());
		assertTrue(id1.hashCode() != id3.hashCode());		
	}
	
}
