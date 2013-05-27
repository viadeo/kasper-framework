package com.viadeo.kasper.query.exposition;

import org.junit.Test;
import static org.junit.Assert.*;

import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.cqrs.query.IQuery;

public class TestTypeAdapterRoundTrips {

	private IQueryFactory factory = new QueryFactoryBuilder().create();
	
	@Test public void testQueryWithPrimitiveArray() {
		PrimitiveArrayQuery expected = new PrimitiveArrayQuery();
		expected.array = new int[]{1, 2, 3};
		ITypeAdapter<PrimitiveArrayQuery> adapter = factory.create(TypeToken.of(PrimitiveArrayQuery.class));
		
		QueryBuilder qBuilder = new QueryBuilder();
		adapter.adapt(expected, qBuilder);
		PrimitiveArrayQuery actual = adapter.adapt(new QueryParser(qBuilder.build()));
		
		assertArrayEquals(expected.array, actual.array);
	}
	
	public static class PrimitiveArrayQuery implements IQuery {
		private static final long serialVersionUID = 1604748331409564661L;
		
		private int[] array;

		public int[] getArray() {
			return array;
		}

		public void setArray(int[] array) {
			this.array = array;
		}
	}
}
