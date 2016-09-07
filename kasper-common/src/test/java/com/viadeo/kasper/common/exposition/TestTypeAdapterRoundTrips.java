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
package com.viadeo.kasper.common.exposition;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.common.exposition.query.QueryBuilder;
import com.viadeo.kasper.common.exposition.query.QueryFactory;
import com.viadeo.kasper.common.exposition.query.QueryFactoryBuilder;
import com.viadeo.kasper.common.exposition.query.QueryParser;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TestTypeAdapterRoundTrips {

	private QueryFactory factory = new QueryFactoryBuilder().create();

    // ------------------------------------------------------------------------

    @Test
    public void testNoExceptionOnBeanWithNoDiscoveredProperties() {
        factory.create(TypeToken.of(EmptyBean.class));
    }

	@Test
    public void testQueryWithDefaultValues() throws Exception {
        // Given
	    final TypeAdapter<QueryWithDefaultValue> adapter = factory.create(TypeToken.of(QueryWithDefaultValue.class));

        // When
	    QueryWithDefaultValue query = adapter.adapt(new QueryParser());

        // Then
	    assertEquals(10, query.getValue());
	}
	
	@Test
    public void testQueryWithPrimitiveArray() throws Exception {

        // Given
		final PrimitiveArrayQuery expected = new PrimitiveArrayQuery();
		expected.array = new int[]{1, 2, 3};

		final TypeAdapter<PrimitiveArrayQuery> adapter = factory.create(TypeToken.of(PrimitiveArrayQuery.class));
		final QueryBuilder qBuilder = new QueryBuilder();

        // When
		adapter.adapt(expected, qBuilder);

        //  Adapt a new mutable map
		final PrimitiveArrayQuery actual = adapter.adapt(new QueryParser(
                LinkedHashMultimap.create(qBuilder.build())));

        // Then
		assertArrayEquals(expected.array, actual.array);
	}
	
    // ------------------------------------------------------------------------

    public static class EmptyBean implements Query {

    }

	public static class QueryWithDefaultValue implements Query {
        private static final long serialVersionUID = 6077221562941902221L;
        
        private int value = 10;

        public int getValue() {
            return value;
        }

        public void setValue(final int value) {
            this.value = value;
        }
	}
	
	public static class PrimitiveArrayQuery implements Query {
		private static final long serialVersionUID = 1604748331409564661L;
		
		private int[] array;

		public int[] getArray() {
			return array;
		}

		public void setArray(final int[] array) {
			this.array = array;
		}
	}

}
