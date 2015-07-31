// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query.interceptor;

import com.codahale.metrics.MetricRegistry;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.component.query.AutowiredQueryHandler;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryCache;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.core.component.query.interceptor.cache.CacheInterceptor;
import com.viadeo.kasper.core.component.query.interceptor.cache.CacheInterceptorFactory;
import com.viadeo.kasper.api.component.Domain;
import org.junit.Before;
import org.junit.Test;

import javax.cache.Caching;

import static org.junit.Assert.*;

public class CacheInterceptorFactoryTest {

    private static final long TTL = 1;

    private final CacheInterceptorFactory factory;
    private InterceptorChain<Query, QueryResponse<QueryResult>> chain;

    // ------------------------------------------------------------------------

    @XKasperUnregistered
    @XKasperQueryHandler(domain = DummyDomain.class, cache = @XKasperQueryCache(keys = "someField"))
    public static class WithFilteredFieldsCacheQueryHandler extends AutowiredQueryHandler<DummyQuery, DummyResult> {
        @Override
        public QueryResponse<DummyResult> retrieve(DummyQuery query) {
            return QueryResponse.of(new DummyResult());
        }
    }

    @XKasperUnregistered
    @XKasperQueryHandler(domain = DummyDomain.class, cache = @XKasperQueryCache(ttl = TTL))
    public static class WithCacheQueryHandler extends AutowiredQueryHandler<DummyQuery, DummyResult> {
        @Override
        public QueryResponse<DummyResult> retrieve(DummyQuery query) {
            return QueryResponse.of(new DummyResult());
        }
    }

    @XKasperUnregistered
    @XKasperQueryHandler(domain = DummyDomain.class)
    public static class WithoutCacheQueryHandler extends AutowiredQueryHandler<DummyQuery, DummyResult> {
        @Override
        public QueryResponse<DummyResult> retrieve(DummyQuery query) {
            return QueryResponse.of(new DummyResult());
        }
    }

    public static class DummyQuery implements Query {
        private static final long serialVersionUID = 3528905729942568435L;

        public String someField;
        public int anotherField;

        public DummyQuery() { }

        public DummyQuery(final String someField, final int anotherField) {
            this.someField = someField;
            this.anotherField = anotherField;
        }
    }

    public static class DummyResult implements QueryResult {
        private static final long serialVersionUID = -8799094444094294006L;
    }

    @XKasperUnregistered
    public static class DummyDomain implements Domain { }

    // ------------------------------------------------------------------------

    public CacheInterceptorFactoryTest() {
        factory = new CacheInterceptorFactory(Caching.getCacheManager());
        KasperMetrics.setMetricRegistry(new MetricRegistry());
    }

    @Before
    public void setUp() {
        chain = factory.create(TypeToken.of(WithCacheQueryHandler.class)).get().withNextChain(
                InterceptorChain.makeChain(new QueryHandlerInterceptor(new WithCacheQueryHandler()))
        );
    }

    // ------------------------------------------------------------------------

    @Test
    public void testFactoryForQueryHandlerWithoutCache() {
        assertFalse(factory.create(TypeToken.of(WithoutCacheQueryHandler.class)).isPresent());
    }

    @Test
    public void testFactoryForQueryHandlerWithCache() {
        InterceptorChain<Query, QueryResponse<QueryResult>> actorsChain = factory.create(TypeToken.of(WithCacheQueryHandler.class)).get();
        assertEquals(CacheInterceptor.class, actorsChain.actor.get().getClass());
    }

    @Test
    public void testObjectRetrievedFromCache() throws Exception {
        // Given
        final DummyQuery nullFields = new DummyQuery();

        // When
        final QueryResponse<QueryResult> expected = chain.next(nullFields, Contexts.empty());
        final QueryResponse<QueryResult> actual = chain.next(nullFields, Contexts.empty());
        final QueryResponse<QueryResult> anotherNotPresentInCache = chain.next(new DummyQuery(), Contexts.empty());

        // Then
        assertSame(expected.getResult(), actual.getResult());
        assertNotSame(expected.getResult(), anotherNotPresentInCache.getResult());
    }

    @Test
    public void testTTLUsedInExpirationPolicy() throws Exception {
        // Given
        final DummyQuery nullFields = new DummyQuery();

        // When
        final QueryResponse<QueryResult> expected = chain.next(nullFields, Contexts.empty());
        final QueryResponse<QueryResult> actual = chain.next(nullFields, Contexts.empty());

        // Wait
        synchronized (this) {
            this.wait(TTL * 1100);
        }

        // And
        final QueryResponse<QueryResult> shouldBeNewAsExpiredFromCache = chain.next(nullFields, Contexts.empty());

        // Then
        assertSame(expected.getResult(), actual.getResult());
        assertNotSame(expected.getResult(), shouldBeNewAsExpiredFromCache.getResult());
    }

    @Test
    public void testKeyGeneratedBasedOnSetOfFields() throws Exception {
        // Given
        chain = factory.create(TypeToken.of(WithFilteredFieldsCacheQueryHandler.class)).get().withNextChain(
                InterceptorChain.makeChain(new QueryHandlerInterceptor(new WithCacheQueryHandler()))
        );

        // When
        final QueryResponse<QueryResult> expected = chain.next(new DummyQuery("aa", 2), Contexts.empty());
        final QueryResponse<QueryResult> actual = chain.next(new DummyQuery("aa", 3333), Contexts.empty());

        // Then
        assertSame(expected.getResult(), actual.getResult());
    }

}

