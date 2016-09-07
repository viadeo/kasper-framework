// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
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
