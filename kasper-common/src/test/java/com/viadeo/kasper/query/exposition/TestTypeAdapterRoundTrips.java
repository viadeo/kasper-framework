// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.query.exposition.query.QueryBuilder;
import com.viadeo.kasper.query.exposition.query.QueryFactory;
import com.viadeo.kasper.query.exposition.query.QueryFactoryBuilder;
import com.viadeo.kasper.query.exposition.query.QueryParser;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TestTypeAdapterRoundTrips {

	private QueryFactory factory = new QueryFactoryBuilder().create();

    // issue #67
    @Test public void testNoExceptionOnBeanWithNoDiscoveredProperties() {
        factory.create(TypeToken.of(EmptyBean.class));
    }

	@Test public void testQueryWithDefaultValues() throws Exception {
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
		PrimitiveArrayQuery actual = adapter.adapt(new QueryParser(
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

        public void setValue(int value) {
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
