// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client;

import java.lang.reflect.Type;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import com.google.common.collect.ImmutableMap;
import com.viadeo.kasper.client.lib.DefaultTypeAdapters;
import com.viadeo.kasper.client.lib.IQueryFactory;
import com.viadeo.kasper.client.lib.ITypeAdapterFactory;
import com.viadeo.kasper.client.lib.QueryBuilder;
import com.viadeo.kasper.client.lib.StdQueryFactory;
import com.viadeo.kasper.client.lib.TypeAdapter;
import com.viadeo.kasper.client.lib.TypeToken;
import com.viadeo.kasper.client.lib.VisibilityFilter;
import com.viadeo.kasper.cqrs.query.IQuery;

public class TestStdQueryFactory {
	
    private QueryBuilder builder;

    // ------------------------------------------------------------------------
    
    class SomeQuery implements IQuery {
        private static final long serialVersionUID = -6763165103363988454L;

        public int getDummy() {
            return 1;
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
        create().skipNull().adapt(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithoutSkipNull() {
        create().adapt(null, null);
    }

    @Test
    public void testCustomQueryAdapterResolution() {
        final TypeAdapter<SomeQuery> adapter = create();
        assertEquals(adapter, createQueryFactory(adapter).create(TypeToken.typeFor(SomeQuery.class)));
    }

    @Test
    public void testCustomQueryAdapterOutput() {
    	
    	// Given
        final TypeAdapter<SomeQuery> adapter = create();
        
        // When
        createQueryFactory(adapter).create(TypeToken.typeFor(SomeQuery.class)).adapt(new SomeQuery(), builder);
        
        // Then
        assertEquals("bar", builder.first("foo"));
    }

    @Test
    public void testBeanQueryAdapterOutputWithPrimitiveIntAdapter() {
    	// Given
        final TypeAdapter<SomeQuery> adapter = new StdQueryFactory(
                ImmutableMap.<Type, TypeAdapter<?>> of(int.class, DefaultTypeAdapters.NUMBER_ADAPTER),
                new ArrayList<ITypeAdapterFactory>(),
                VisibilityFilter.PACKAGE_PUBLIC).create(TypeToken.typeFor(SomeQuery.class));
        
        // When
        adapter.adapt(new SomeQuery(), builder);
        
        // Then
        assertEquals("1", builder.first("dummy"));
    }

    @Test
    public void testQueryFactoryOutputWithCollectionAdapter() {
    	// TODO ?
    }

    private IQueryFactory createQueryFactory(TypeAdapter<SomeQuery> adapter) {
        return new StdQueryFactory(
                ImmutableMap.<Type, TypeAdapter<?>> of(SomeQuery.class, adapter),
                new ArrayList<ITypeAdapterFactory>(),
                VisibilityFilter.PACKAGE_PUBLIC);
    }

    private TypeAdapter<SomeQuery> create() {
        return new TypeAdapter<SomeQuery>() {
            @Override
            public void adapt(SomeQuery value, QueryBuilder builder) {
                if (value == null)
                    throw new IllegalArgumentException();
                builder.addSingle("foo", "bar");
            }
        };
    }

}
