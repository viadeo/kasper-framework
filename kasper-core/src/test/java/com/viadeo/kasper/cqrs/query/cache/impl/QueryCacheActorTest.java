// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.cache.impl;

import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.cqrs.RequestActorsChain;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryCache;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.cqrs.query.impl.AbstractQueryHandler;
import com.viadeo.kasper.cqrs.query.impl.QueryHandlerActor;
import com.viadeo.kasper.ddd.Domain;
import org.junit.Before;
import org.junit.Test;

import javax.cache.Caching;

import static org.junit.Assert.*;

public class QueryCacheActorTest {

    final static long TTL = 1;

    private AnnotationQueryCacheActorFactory factory;
    private RequestActorsChain<DummyQuery, QueryResponse<DummyResult>> chain;

    // ------------------------------------------------------------------------

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        factory = new AnnotationQueryCacheActorFactory(Caching.getCacheManager());
        chain = RequestActorsChain.makeChain(
                (QueryCacheActor<DummyQuery, DummyResult>)
                        factory.make(
                                DummyQuery.class,
                                WithCacheQueryHandler.class
                        ).get(),
                new QueryHandlerActor<>(new WithCacheQueryHandler()));
    }

    @Test
    public void testFactoryForQueryHandlerWithoutCache() {
        assertFalse(factory.make(DummyQuery.class, WithoutCacheQueryHandler.class).isPresent());
    }

    @Test
    public void testFactoryForQueryHandlerWithCache() {
        assertEquals(QueryCacheActor.class, factory.make(DummyQuery.class, WithCacheQueryHandler.class).get().getClass());
    }

    @Test
    public void testObjectRetrievedFromCache() throws Exception {
        // Given
        final DummyQuery nullFields = new DummyQuery();

        // When
        final QueryResponse<DummyResult> expected = chain.next(nullFields, new DefaultContext());
        final QueryResponse<DummyResult> actual = chain.next(nullFields, new DefaultContext());
        final QueryResponse<DummyResult> anotherNotPresentInCache = chain.next(new DummyQuery(), new DefaultContext());

        // Then
        assertSame(expected, actual);
        assertNotSame(expected, anotherNotPresentInCache);
    }

    @Test
    public void testTTLUsedInExpirationPolicy() throws Exception {
        // Given
        final DummyQuery nullFields = new DummyQuery();

        // When
        final QueryResponse<DummyResult> expected = chain.next(nullFields, new DefaultContext());
        final QueryResponse<DummyResult> actual = chain.next(nullFields, new DefaultContext());

        // Wait
        synchronized (this) {
            this.wait(TTL * 1100);
        }

        // And
        final QueryResponse<DummyResult> shouldBeNewAsExpiredFromCache = chain.next(nullFields, new DefaultContext());

        // Then
        assertSame(expected, actual);
        assertNotSame(expected, shouldBeNewAsExpiredFromCache);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testKeyGeneratedBasedOnSetOfFields() throws Exception {
        // Given
        chain = RequestActorsChain.makeChain(
                (QueryCacheActor<DummyQuery, DummyResult>) factory.make(
                        DummyQuery.class,
                        WithFilteredFieldsCacheQueryHandler.class).get(),
                new QueryHandlerActor<>(new WithFilteredFieldsCacheQueryHandler()));

        // When
        final QueryResponse<DummyResult> expected = chain.next(new DummyQuery("aa", 2), new DefaultContext());
        final QueryResponse<DummyResult> actual = chain.next(new DummyQuery("aa", 3333), new DefaultContext());

        // Then
        assertSame(expected, actual);
    }

    // ------------------------------------------------------------------------

    @XKasperUnregistered
    @XKasperQueryHandler(domain = DummyDomain.class, cache = @XKasperQueryCache(keys = "someField"))
    public static class WithFilteredFieldsCacheQueryHandler extends AbstractQueryHandler<DummyQuery, DummyResult> {
        @Override
        public QueryResponse<DummyResult> retrieve(DummyQuery query) throws Exception {
            return QueryResponse.of(new DummyResult());
        }
    }

    @XKasperUnregistered
    @XKasperQueryHandler(domain = DummyDomain.class, cache = @XKasperQueryCache(ttl = TTL))
    public static class WithCacheQueryHandler extends AbstractQueryHandler<DummyQuery, DummyResult> {
        @Override
        public QueryResponse<DummyResult> retrieve(DummyQuery query) throws Exception {
            return QueryResponse.of(new DummyResult());
        }
    }

    @XKasperUnregistered
    @XKasperQueryHandler(domain = DummyDomain.class)
    public static class WithoutCacheQueryHandler extends AbstractQueryHandler<DummyQuery, DummyResult> {
        @Override
        public QueryResponse<DummyResult> retrieve(DummyQuery query) throws Exception {
            return QueryResponse.of(new DummyResult());
        }
    }

    public static class DummyQuery implements Query {
        public String someField;
        public int anotherField;

        public DummyQuery() { }

        public DummyQuery(final String someField, final int anotherField) {
            this.someField = someField;
            this.anotherField = anotherField;
        }
    }

    public static class DummyResult implements QueryResult { }

    @XKasperUnregistered
    public static class DummyDomain implements Domain { }

}

