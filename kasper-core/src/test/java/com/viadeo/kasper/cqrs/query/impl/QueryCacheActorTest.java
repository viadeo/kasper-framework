package com.viadeo.kasper.cqrs.query.impl;

import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryCache;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.ddd.Domain;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


import javax.cache.Caching;

public class QueryCacheActorTest {

    final static long TTL = 1;

    private QueryCacheActor.AnnotationQueryCacheActorFactory factory;
    private RequestActorChain<DummyQuery, QueryResult<DummyPayload>> chain;

    @Before
    public void setUp() {
        factory = new QueryCacheActor.AnnotationQueryCacheActorFactory(Caching.getCacheManager());
        chain = RequestActorChain.makeChain(
                (QueryCacheActor<DummyQuery, DummyPayload>) factory.make(DummyQuery.class, WithCacheQueryService.class),
                new QueryServiceActor<DummyQuery, DummyPayload>(new WithCacheQueryService()));
    }

    @Test
    public void testFactoryForQueryServiceWithoutCache() {
        assertNull(factory.make(DummyQuery.class, WithoutCacheQueryService.class));
    }

    @Test
    public void testFactoryForQueryServiceWithCache() {
        assertEquals(QueryCacheActor.class, factory.make(DummyQuery.class, WithCacheQueryService.class).getClass());
    }

    @Test
    public void testObjectRetrievedFromCache() throws Exception {
        DummyQuery nullFields = new DummyQuery();

        QueryResult<DummyPayload> expected = chain.next(nullFields, new DefaultContext());
        QueryResult<DummyPayload> actual = chain.next(nullFields, new DefaultContext());
        QueryResult<DummyPayload> anotherNotPresentInCache = chain.next(new DummyQuery(), new DefaultContext());

        assertSame(expected, actual);
        assertNotSame(expected, anotherNotPresentInCache);
    }

    @Test
    public void testTTLUsedInExpirationPolicy() throws Exception {
        DummyQuery nullFields = new DummyQuery();

        QueryResult<DummyPayload> expected = chain.next(nullFields, new DefaultContext());
        QueryResult<DummyPayload> actual = chain.next(nullFields, new DefaultContext());

        synchronized (this) {
            this.wait(TTL * 1100);
        }

        QueryResult<DummyPayload> shouldBeNewAsExpiredFromCache = chain.next(nullFields, new DefaultContext());

        assertSame(expected, actual);
        assertNotSame(expected, shouldBeNewAsExpiredFromCache);
    }

    @Test
    public void testKeyGeneratedBasedOnSetOfFields() throws Exception {
        chain = RequestActorChain.makeChain(
                (QueryCacheActor<DummyQuery, DummyPayload>) factory.make(DummyQuery.class, WithFilteredFieldsCacheQueryService.class),
                new QueryServiceActor<>(new WithFilteredFieldsCacheQueryService()));

        QueryResult<DummyPayload> expected = chain.next(new DummyQuery("aa", 2), new DefaultContext());
        QueryResult<DummyPayload> actual = chain.next(new DummyQuery("aa", 3333), new DefaultContext());

        assertSame(expected, actual);
    }

    @XKasperQueryService(domain = DummyDomain.class, cache = @XKasperQueryCache(keys = "someField"))
    public static class WithFilteredFieldsCacheQueryService extends AbstractQueryService<DummyQuery, DummyPayload> {
        @Override
        public QueryResult<DummyPayload> retrieve(DummyQuery query) throws Exception {
            return QueryResult.of(new DummyPayload());
        }
    }

    @XKasperQueryService(domain = DummyDomain.class, cache = @XKasperQueryCache(ttl = TTL))
    public static class WithCacheQueryService extends AbstractQueryService<DummyQuery, DummyPayload> {
        @Override
        public QueryResult<DummyPayload> retrieve(DummyQuery query) throws Exception {
            return QueryResult.of(new DummyPayload());
        }
    }

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

        public DummyQuery() {
        }

        public DummyQuery(String someField, int anotherField) {
            this.someField = someField;
            this.anotherField = anotherField;
        }
    }

    public static class DummyPayload implements QueryPayload {

    }

    public static class DummyDomain implements Domain {

    }
}
