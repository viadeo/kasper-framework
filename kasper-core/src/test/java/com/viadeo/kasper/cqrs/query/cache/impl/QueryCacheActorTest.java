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
import com.viadeo.kasper.cqrs.query.QueryPayload;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryCache;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.cqrs.query.impl.AbstractQueryService;
import com.viadeo.kasper.cqrs.query.impl.QueryServiceActor;
import com.viadeo.kasper.ddd.Domain;
import org.junit.Before;
import org.junit.Test;

import javax.cache.Caching;

import static org.junit.Assert.*;

public class QueryCacheActorTest {

    final static long TTL = 1;

    private AnnotationQueryCacheActorFactory factory;
    private RequestActorsChain<DummyQuery, QueryResult<DummyPayload>> chain;

    // ------------------------------------------------------------------------

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        factory = new AnnotationQueryCacheActorFactory(Caching.getCacheManager());
        chain = RequestActorsChain.makeChain(
                (QueryCacheActor<DummyQuery, DummyPayload>)
                        factory.make(
                                DummyQuery.class,
                                WithCacheQueryService.class
                        ).get(),
                new QueryServiceActor<>(new WithCacheQueryService()));
    }

    @Test
    public void testFactoryForQueryServiceWithoutCache() {
        assertFalse(factory.make(DummyQuery.class, WithoutCacheQueryService.class).isPresent());
    }

    @Test
    public void testFactoryForQueryServiceWithCache() {
        assertEquals(QueryCacheActor.class, factory.make(DummyQuery.class, WithCacheQueryService.class).get().getClass());
    }

    @Test
    public void testObjectRetrievedFromCache() throws Exception {
        // Given
        final DummyQuery nullFields = new DummyQuery();

        // When
        final QueryResult<DummyPayload> expected = chain.next(nullFields, new DefaultContext());
        final QueryResult<DummyPayload> actual = chain.next(nullFields, new DefaultContext());
        final QueryResult<DummyPayload> anotherNotPresentInCache = chain.next(new DummyQuery(), new DefaultContext());

        // Then
        assertSame(expected, actual);
        assertNotSame(expected, anotherNotPresentInCache);
    }

    @Test
    public void testTTLUsedInExpirationPolicy() throws Exception {
        // Given
        final DummyQuery nullFields = new DummyQuery();

        // When
        final QueryResult<DummyPayload> expected = chain.next(nullFields, new DefaultContext());
        final QueryResult<DummyPayload> actual = chain.next(nullFields, new DefaultContext());

        // Wait
        synchronized (this) {
            this.wait(TTL * 1100);
        }

        // And
        final QueryResult<DummyPayload> shouldBeNewAsExpiredFromCache = chain.next(nullFields, new DefaultContext());

        // Then
        assertSame(expected, actual);
        assertNotSame(expected, shouldBeNewAsExpiredFromCache);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testKeyGeneratedBasedOnSetOfFields() throws Exception {
        // Given
        chain = RequestActorsChain.makeChain(
                (QueryCacheActor<DummyQuery, DummyPayload>) factory.make(
                        DummyQuery.class,
                        WithFilteredFieldsCacheQueryService.class).get(),
                new QueryServiceActor<>(new WithFilteredFieldsCacheQueryService()));

        // When
        final QueryResult<DummyPayload> expected = chain.next(new DummyQuery("aa", 2), new DefaultContext());
        final QueryResult<DummyPayload> actual = chain.next(new DummyQuery("aa", 3333), new DefaultContext());

        // Then
        assertSame(expected, actual);
    }

    // ------------------------------------------------------------------------

    @XKasperUnregistered
    @XKasperQueryService(domain = DummyDomain.class, cache = @XKasperQueryCache(keys = "someField"))
    public static class WithFilteredFieldsCacheQueryService extends AbstractQueryService<DummyQuery, DummyPayload> {
        @Override
        public QueryResult<DummyPayload> retrieve(DummyQuery query) throws Exception {
            return QueryResult.of(new DummyPayload());
        }
    }

    @XKasperUnregistered
    @XKasperQueryService(domain = DummyDomain.class, cache = @XKasperQueryCache(ttl = TTL))
    public static class WithCacheQueryService extends AbstractQueryService<DummyQuery, DummyPayload> {
        @Override
        public QueryResult<DummyPayload> retrieve(DummyQuery query) throws Exception {
            return QueryResult.of(new DummyPayload());
        }
    }

    @XKasperUnregistered
    @XKasperQueryService(domain = DummyDomain.class)
    public static class WithoutCacheQueryService extends AbstractQueryService<DummyQuery, DummyPayload> {
        @Override
        public QueryResult<DummyPayload> retrieve(DummyQuery query) throws Exception {
            return QueryResult.of(new DummyPayload());
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

    public static class DummyPayload implements QueryPayload { }

    @XKasperUnregistered
    public static class DummyDomain implements Domain { }

}

