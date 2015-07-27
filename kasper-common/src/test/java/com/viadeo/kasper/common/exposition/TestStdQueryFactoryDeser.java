// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.exposition;

import com.google.common.collect.*;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.common.exposition.FeatureConfiguration;
import com.viadeo.kasper.common.exposition.TypeAdapter;
import com.viadeo.kasper.common.exposition.adapters.DefaultTypeAdapters;
import com.viadeo.kasper.common.exposition.query.*;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.*;

import static org.junit.Assert.*;

public class TestStdQueryFactoryDeser {

    private DefaultQueryFactory factory;

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        final Map<Type, TypeAdapter> adapters = new HashMap<Type, TypeAdapter>();
        adapters.put(String.class, DefaultTypeAdapters.STRING_ADAPTER);
        adapters.put(int.class, DefaultTypeAdapters.INT_ADAPTER);

        factory = new DefaultQueryFactory(new FeatureConfiguration(), adapters, ImmutableMap.<Type, BeanAdapter>of(),
                Arrays.asList(DefaultTypeAdapters.COLLECTION_ADAPTER_FACTORY),
                VisibilityFilter.PACKAGE_PUBLIC);
    }

    @Test
    public void testDeserDateTimeFromMillis() throws Exception {
        final DateTime now = DateTime.now();

        final DateTime actual = DefaultTypeAdapters.DATETIME_ADAPTER.adapt(
                new QueryParser(Multimaps.forMap(
                        ImmutableMap.of("now", "" + now.getMillis())
                )
            ).begin("now"));

        assertEquals(now, actual);
    }

    @Test
    public void testDeserDateTimeFromISO() throws Exception {
        final DateTime now = DateTime.now();

        final DateTime actual = DefaultTypeAdapters.DATETIME_ADAPTER.adapt(
                new QueryParser(Multimaps.forMap(
                        ImmutableMap.of("now", now.toString())
                )
            ).begin("now"));

        assertEquals(now, actual);
    }

    @Test
    public void testSimpleQueryDeserialization() throws Exception {

        // Given
        final TypeAdapter<SimpleQuery> adapter = factory.create(TypeToken.of(SimpleQuery.class));
        final SetMultimap<String, String> given = LinkedHashMultimap
                .create(
                        new ImmutableSetMultimap.Builder<String, String>()
                            .put("name", "foo")
                            .put("age", "1")
                            .putAll("list", Arrays.asList("bar", "barfoo", "foobar"))
                            .build()
                );

        // When
        final SimpleQuery query = adapter.adapt(new QueryParser(given));

        // Then
        assertEquals("foo", query.name);
        assertEquals(1, query.age);
        assertEquals(Arrays.asList("bar", "barfoo", "foobar"), query.list);
    }

    @Test
    public void testComposedQuery() throws Exception {

        // Given
        final SetMultimap<String, String> given = LinkedHashMultimap
                .create(
                        new ImmutableSetMultimap.Builder<String, String>()
                            .put("field", "someValue")
                            .put("name", "foo")
                            .put("age", "1")
                            .putAll("list", Arrays.asList("bar", "barfoo", "foobar"))
                            .build()
                );

        final TypeAdapter<ComposedQuery> adapter = factory
                .create(TypeToken.of(ComposedQuery.class));

        // When
        final ComposedQuery query = adapter.adapt(new QueryParser(given));

        // Then
        assertEquals("someValue", query.field);
        assertEquals("foo", query.query.name);
        assertEquals(1, query.query.age);
        assertEquals(Arrays.asList("bar", "barfoo", "foobar"), query.query.list);
    }

    @Test
    public void testDeserializeWithMissingAndAdditionalFields() throws Exception {

        // Given
        final TypeAdapter<SimpleQuery> adapter = factory.create(TypeToken.of(SimpleQuery.class));

        // When
        final SimpleQuery query = adapter.adapt(new QueryParser(LinkedHashMultimap.create(
                new ImmutableSetMultimap.Builder<String, String>()
                        .put("field", "someValue")
                        .put("name", "foo")
                        .build()
        )));

        // Then
        assertEquals("foo", query.name);
        assertNull(query.list);
        assertEquals(0, query.age);
    }

    @Test
    public void testDeserForComplexObjectWithCustomAdapter() throws Exception {
        // needed when a custom typeadapter is registered and does not serialize
        // to a literal
        // Given
        final QueryFactory factory = new QueryFactoryBuilder().use(new SomeBeanAdapter()).create();
        final TypeAdapter<BaseQuery> adapter = factory.create(TypeToken.of(BaseQuery.class));

        // When
        final BaseQuery q = adapter.adapt(new QueryParser(LinkedHashMultimap.create(
                new ImmutableSetMultimap.Builder<String, String>()
                        .put("list_foo", "bar")
                        .build()
        )));

        // Then
        assertNotNull(q.list);
        assertTrue(q.list.size() == 1);
        assertEquals("foo", q.list.get(0).key);
        assertEquals("bar", q.list.get(0).value);
    }

    // ------------------------------------------------------------------------

    public static class ComposedQuery implements Query {
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

    public static class SomeBeanAdapter implements BeanAdapter<List<SomeBean>> {

        @Override
        public void adapt(final List<SomeBean> value, final QueryBuilder builder, final BeanProperty property) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<SomeBean> adapt(final QueryParser parser, final BeanProperty property) {
            final String prefix = property.getName() + "_";
            final List<SomeBean> list = new ArrayList<SomeBean>();
            for (final String name : parser.names()) {
                if (name.startsWith(prefix)) {
                    parser.begin(name);
                    list.add(new SomeBean(name.replace(prefix, ""), parser.value()));
                    parser.end();
                }
            }
            return list;
        }

    }

    public static class BaseQuery implements Query {
        private static final long serialVersionUID = -7064625946045395703L;
        private List<SomeBean> list;

        public List<SomeBean> getList() {
            return list;
        }

        public void setList(final List<SomeBean> list) {
            this.list = list;
        }
    }

    public static class SomeBean {
        private String key;
        private String value;

        public SomeBean(final String key, final String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(final String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(final String value) {
            this.value = value;
        }
    }

    public static class SimpleQuery implements Query {
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
