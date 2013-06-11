// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition;

import java.util.HashMap;
import java.util.List;

import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.cqrs.query.IQuery;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestTypeAdapterRoundTrips {

	private IQueryFactory factory = new QueryFactoryBuilder().create();
	
	@Test public void testQueryWithDefaultValues() throws Exception {
	    final ITypeAdapter<QueryWithDefaultValue> adapter = factory.create(TypeToken.of(QueryWithDefaultValue.class));
	    
	    QueryWithDefaultValue query = adapter.adapt(new QueryParser(new HashMap<String, List<String>>()));
	    
	    assertEquals(10, query.getValue());
	}
	
	@Test
    public void testQueryWithPrimitiveArray() throws Exception {

        // Given
		final PrimitiveArrayQuery expected = new PrimitiveArrayQuery();
		expected.array = new int[]{1, 2, 3};
		final ITypeAdapter<PrimitiveArrayQuery> adapter = factory.create(TypeToken.of(PrimitiveArrayQuery.class));
		final QueryBuilder qBuilder = new QueryBuilder();

        // When
		adapter.adapt(expected, qBuilder);
		PrimitiveArrayQuery actual = adapter.adapt(new QueryParser(qBuilder.build()));

        // Then
		assertArrayEquals(expected.array, actual.array);
	}
	
    // ------------------------------------------------------------------------

	public static class QueryWithDefaultValue implements IQuery {
        private static final long serialVersionUID = 6077221562941902221L;
        
        private int value = 10;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
	}
	
	public static class PrimitiveArrayQuery implements IQuery {
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
