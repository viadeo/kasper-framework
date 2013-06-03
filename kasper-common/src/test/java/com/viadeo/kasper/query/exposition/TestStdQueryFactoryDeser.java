// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.cqrs.query.IQuery;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestStdQueryFactoryDeser {
	private StdQueryFactory factory;

	@Before
	public void setUp() {
		final Map<Type, ITypeAdapter<?>> adapters = new HashMap<>();
		adapters.put(String.class, DefaultTypeAdapters.STRING_ADAPTER);
		adapters.put(int.class, DefaultTypeAdapters.INT_ADAPTER);

		factory = new StdQueryFactory(adapters,
				Arrays.asList(DefaultTypeAdapters.COLLECTION_ADAPTER_FACTORY),
				VisibilityFilter.PACKAGE_PUBLIC);
	}

	@Test
	public void testSimpleQueryDeserialization() {

        // Given
		final ITypeAdapter<SimpleQuery> adapter = factory.create(TypeToken.of(SimpleQuery.class));
		final Map<String, List<String>> given = ImmutableMap.of("name",
				Arrays.asList("foo"), "age", Arrays.asList("1"), "list",
				Arrays.asList("bar", "barfoo", "foobar"));

        // When
		final SimpleQuery query = adapter.adapt(new QueryParser(given));

        // Then
		assertEquals("foo", query.name);
		assertEquals(1, query.age);
		assertEquals(Arrays.asList("bar", "barfoo", "foobar"), query.list);
	}

	@Test
	public void testComposedQuery() {

        // Given
		final Map<String, List<String>> given = ImmutableMap.of("field",
				Arrays.asList("someValue"), "name", Arrays.asList("foo"),
				"age", Arrays.asList("1"), "list",
				Arrays.asList("bar", "barfoo", "foobar"));
		final ITypeAdapter<ComposedQuery> adapter = factory.create(TypeToken
				.of(ComposedQuery.class));

        // When
		final ComposedQuery query = adapter.adapt(new QueryParser(given));

        // Then
		assertEquals("someValue", query.field);
		assertEquals("foo", query.query.name);
		assertEquals(1, query.query.age);
		assertEquals(Arrays.asList("bar", "barfoo", "foobar"), query.query.list);
	}

	@Test
	public void testDeserializeWithMissingAndAdditionalFields() {

        // Given
		final ITypeAdapter<SimpleQuery> adapter = factory.create(TypeToken.of(SimpleQuery.class));

        // When
		SimpleQuery query = adapter.adapt(new QueryParser(ImmutableMap.of(
				"field", Arrays.asList("someValue"), "name",
				Arrays.asList("foo"))));

        // Then
		assertEquals("foo", query.name);
		assertNull(query.list);
		assertEquals(0, query.age);
	}

    // ------------------------------------------------------------------------

	public static class ComposedQuery implements IQuery {
		private static final long serialVersionUID = 5434689745780198187L;

		private SimpleQuery query;
		private String field;

		public ComposedQuery(final SimpleQuery query, final String field) {
			this.query = query;
			this.field = field;
		}

		public SimpleQuery getQuery() {
			return query;
		}

		public void setQuery(final SimpleQuery query) {
			this.query = query;
		}

		public String getField() {
			return field;
		}

		public void setField(final String field) {
			this.field = field;
		}

	}

    // ------------------------------------------------------------------------

	public static class SimpleQuery implements IQuery {
		private static final long serialVersionUID = 2101539230768491786L;

		private final String name;
		private final int age;
		private final List<String> list;

		public SimpleQuery(final String name, final int age, final List<String> list) {
			this.name = name;
			this.age = age;
			this.list = list;
		}

		public String getName() {
			return name;
		}

		public int getAge() {
			return age;
		}

		public List<String> getList() {
			return list;
		}
	}

}
