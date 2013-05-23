// ============================================================================
// KASPER - Kasper is the treasure keeper
// www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
// Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.query.exposition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.cqrs.query.IQuery;
import com.viadeo.kasper.query.exposition.DefaultTypeAdapters;
import com.viadeo.kasper.query.exposition.IQueryFactory;
import com.viadeo.kasper.query.exposition.ITypeAdapter;
import com.viadeo.kasper.query.exposition.ITypeAdapterFactory;
import com.viadeo.kasper.query.exposition.NullSafeTypeAdapter;
import com.viadeo.kasper.query.exposition.QueryBuilder;
import com.viadeo.kasper.query.exposition.StdQueryFactory;
import com.viadeo.kasper.query.exposition.VisibilityFilter;

public class TestStdQueryFactory {

    private QueryBuilder builder;

    // ------------------------------------------------------------------------

    public static class SomeQuery implements IQuery {
        private static final long serialVersionUID = -6763165103363988454L;

        public int getDummy() {
            return 1;
        }
        
        public void setDummy(int dummyInt) {
        	
        }
    }

    // ------------------------------------------------------------------------

    @Before
    public void init() {
        builder = new QueryBuilder();
    }

    // ------------------------------------------------------------------------

    @Test
    public void testSkipNull() {
        new NullSafeTypeAdapter<SomeQuery>(create()).adapt(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithoutSkipNull() {
        create().adapt(null, null);
    }

    @Test
    public void testCustomQueryAdapterResolution() {
        final ITypeAdapter<SomeQuery> adapter = create();
        assertEquals(adapter, createQueryFactory(adapter).create(TypeToken.of(SomeQuery.class)));
    }

    @Test
    public void testCustomQueryAdapterOutput() {

        // Given
        final ITypeAdapter<SomeQuery> adapter = create();

        // When
        createQueryFactory(adapter).create(TypeToken.of(SomeQuery.class)).adapt(new SomeQuery(), builder);

        // Then
        assertEquals("bar", builder.first("foo"));
    }

    @Test
    public void testBeanQueryAdapterOutputWithPrimitiveIntAdapter() {
        // Given
        final ITypeAdapter<SomeQuery> adapter = new StdQueryFactory(
                ImmutableMap.<Type, ITypeAdapter<?>> of(int.class, DefaultTypeAdapters.INT_ADAPTER),
                new ArrayList<ITypeAdapterFactory<?>>(),
                VisibilityFilter.PACKAGE_PUBLIC).create(TypeToken.of(SomeQuery.class));

        // When
        adapter.adapt(new SomeQuery(), builder);

        // Then
        assertEquals("1", builder.first("dummy"));
    }

    @Test
    public void testQueryFactoryOutputWithCollectionAdapter() {
        // Given
        final DateTime firstDate = new DateTime();
        final DateTime secondDate = new DateTime();
        final QueryOfDateTimeCollection query = new QueryOfDateTimeCollection(Arrays.asList(firstDate, secondDate));
        final IQueryFactory queryFactory = createQueryFactory(DefaultTypeAdapters.COLLECTION_ADAPTER_FACTORY, DefaultTypeAdapters.DATETIME_ADAPTER);

        // When
        queryFactory.create(TypeToken.of(QueryOfDateTimeCollection.class)).adapt(query, builder);

        // Then
        assertEquals("" + firstDate.getMillis(), builder.values("listOfDateTime").get(0));
        assertEquals("" + secondDate.getMillis(), builder.values("listOfDateTime").get(1));
    }

    @Test
    public void testCustomTypeAdapterFactoryWithDeepGenerics() {
        // Given
        final String key1 = "key1";
        final String key2 = "key2";
        final List<DateTime> key1Values = Arrays.asList(new DateTime(), new DateTime());
        final QueryWithMap query = new QueryWithMap(ImmutableMap.of(key1, key1Values, key2, new ArrayList<DateTime>()));
        final IQueryFactory factory = createQueryFactory(createTypeAdapterFactory(), DefaultTypeAdapters.COLLECTION_ADAPTER_FACTORY, DefaultTypeAdapters.DATETIME_ADAPTER);

        // When
        factory.create(TypeToken.of(QueryWithMap.class)).adapt(query, builder);

        // Then
        assertFalse(builder.has(key2));
        assertTrue(builder.has(key1));
        for (int i = 0; i < key1Values.size(); i++) {
            assertEquals("" + key1Values.get(i).getMillis(), builder.values(key1).get(i));
        }
    }

    // ========================================================================
    
    private ITypeAdapterFactory<Map<String, List<DateTime>>> createTypeAdapterFactory() {
        return new ITypeAdapterFactory<Map<String, List<DateTime>>>() {
            @Override
            public Optional<ITypeAdapter<Map<String, List<DateTime>>>> create(TypeToken<Map<String, List<DateTime>>> typeToken, final IQueryFactory adapterFactory) {
                @SuppressWarnings("serial")
                final ITypeAdapter<List<DateTime>> dateTimeListAdapter = adapterFactory.create(new TypeToken<List<DateTime>>() {});

                final ITypeAdapter<Map<String, List<DateTime>>> adapter = new ITypeAdapter<Map<String, List<DateTime>>>() {
                    @Override
                    public void adapt(final Map<String, List<DateTime>> value, final QueryBuilder builder) {
                        for (final Map.Entry<String, List<DateTime>> entry : value.entrySet()) {
                            builder.begin(entry.getKey());
                            dateTimeListAdapter.adapt(entry.getValue(), builder);
                            builder.end();
                        }
                    }
                    
                    @Override
                    public Map<String, List<DateTime>> adapt(QueryParser parser) {
                    	return null;
                    }
                };
                return Optional.of(adapter);
            }
        };
    }

    private IQueryFactory createQueryFactory(final Object... queryFactoryParameters) {
        final Map<Type, ITypeAdapter<?>> adaptersMap = new HashMap<Type, ITypeAdapter<?>>();
        final List<ITypeAdapterFactory<?>> factories = new ArrayList<ITypeAdapterFactory<?>>();
        
        for (final Object parameter : queryFactoryParameters) {
            if (parameter instanceof ITypeAdapter) {
                final ITypeAdapter<?> adapter = (ITypeAdapter<?>) parameter;
                final TypeToken<?> adapterForType = TypeToken.of(adapter.getClass()).resolveType(ITypeAdapter.class.getTypeParameters()[0]);
                adaptersMap.put(adapterForType.getType(), adapter);
            } else if (parameter instanceof ITypeAdapterFactory) {
                factories.add((ITypeAdapterFactory<?>) parameter);
            } else {
                throw new IllegalArgumentException("Only TypeAdapter or TypeAdapter factories are allowed.");
            }
        }
        
        return new StdQueryFactory(
                adaptersMap,
                factories,
                VisibilityFilter.PACKAGE_PUBLIC);
    }

    private ITypeAdapter<SomeQuery> create() {
        return new ITypeAdapter<SomeQuery>() {
            @Override
            public void adapt(final SomeQuery value, final QueryBuilder builder) {
                if (null == value) {
                    throw new IllegalArgumentException();
                }
                builder.addSingle("foo", "bar");
            }
            
            @Override
            public SomeQuery adapt(QueryParser parser) {
            	return null;
            }
        };
    }

    public static class QueryWithMap implements IQuery {
        private static final long serialVersionUID = 1914912257262499643L;
        private final Map<String, List<DateTime>> mapOfDateTime;

        public QueryWithMap(final Map<String, List<DateTime>> mapOfDateTime) {
            this.mapOfDateTime = mapOfDateTime;
        }

        public Map<String, List<DateTime>> getMapOfDateTime() {
            return this.mapOfDateTime;
        }
    }

    public static class QueryOfDateTimeCollection implements IQuery {
        private static final long serialVersionUID = -6933354147082294343L;

        private final List<DateTime> listOfDateTime;

        public QueryOfDateTimeCollection(final List<DateTime> listOfDateTime) {
            this.listOfDateTime = listOfDateTime;
        }

        public List<DateTime> getListOfDateTime() {
            return this.listOfDateTime;
        }
    }
}
