// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import com.viadeo.kasper.core.interceptor.QueryInterceptor;
import com.viadeo.kasper.core.locators.impl.DefaultQueryHandlersLocator;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryCache;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryFilter;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.cqrs.query.interceptor.CacheInterceptor;
import com.viadeo.kasper.cqrs.query.interceptor.CacheInterceptorFactory;
import com.viadeo.kasper.cqrs.query.interceptor.QueryFilterInterceptorFactory;
import com.viadeo.kasper.cqrs.query.interceptor.QueryHandlerInterceptor;
import com.viadeo.kasper.ddd.Domain;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;

public class KasperQueryGatewayUTest {

    private final KasperQueryGateway queryGateway;
    private final DefaultQueryHandlersLocator queryHandlersLocator;
    private final InterceptorChainRegistry interceptorChainRegistry;

    @XKasperQueryHandler(domain = Domain.class)
    private static class QueryHandlerForTest extends QueryHandler<Query, QueryResult> { }

    @XKasperQueryHandler(domain = Domain.class)
    @XKasperQueryFilter(value = {InterceptorA.class})
    private static class QueryHandlerWithFiltersForTest extends QueryHandler<Query, QueryResult> { }

    @XKasperQueryHandler(domain = Domain.class, cache = @XKasperQueryCache(enabled = true))
    private static class QueryHandlerWithCacheForTest extends QueryHandler<Query, QueryResult> { }

    public static class InterceptorA implements QueryInterceptor<Query, QueryResult> {
        @Override
        public QueryResponse<QueryResult> process(Query query, Context context, InterceptorChain<Query, QueryResponse<QueryResult>> chain) throws Exception {
            return chain.next(query, context);
        }
    }

    // ------------------------------------------------------------------------

    public KasperQueryGatewayUTest() {
        queryHandlersLocator = mock(DefaultQueryHandlersLocator.class);
        interceptorChainRegistry = mock(InterceptorChainRegistry.class);
        queryGateway = new KasperQueryGateway(queryHandlersLocator, interceptorChainRegistry);
    }

    @After
    public void clean() {
        reset(queryHandlersLocator, interceptorChainRegistry);
    }

    // ------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void register_withNullAsQueryHandler_shouldThrownException() {
        // Given
        final QueryHandler queryHandler = null;

        // When
        queryGateway.register(queryHandler);

        // Then throws an exception
    }

    @Test
    public void register_withQueryHandler_shouldBeRegistered() {
        // Given
        final QueryHandler queryHandler = new QueryHandlerForTest();

        // When
        queryGateway.register(queryHandler);

        // Then
        verify(queryHandlersLocator).registerHandler(refEq("QueryHandlerForTest"), refEq(queryHandler), refEq(Domain.class));
        verifyNoMoreInteractions(queryHandlersLocator);

        assertEquals(queryGateway, queryHandler.getQueryGateway());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getInterceptorChain_withUnknownQuery_shouldReturnNoChain() {
        // Given
        when(interceptorChainRegistry.get(any(Class.class))).thenReturn(Optional.absent());
        when(queryHandlersLocator.getHandlerFromQueryClass(any(Class.class))).thenReturn(Optional.<QueryHandler<Query, QueryResult>>absent());


        // When
        final Optional<InterceptorChain<Query,QueryResponse<QueryResult>>> requestActorChain =
                queryGateway.getInterceptorChain(Query.class);

        // Then
        assertNotNull(requestActorChain);
        assertFalse(requestActorChain.isPresent());
    }

    @Test
    public void getInterceptorChain_withQueryHandler_shouldBeOk() {
        // Given
        final QueryHandler<Query,QueryResult> queryHandler = new QueryHandlerWithFiltersForTest();

        final KasperQueryGateway queryGateway = new KasperQueryGateway();
        queryGateway.register(queryHandler);

        // When
        final Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> interceptorChain =
                queryGateway.getInterceptorChain(Query.class);

        // Then
        assertTrue(interceptorChain.isPresent());
    }

    @Test
    public void getInterceptorChain_withQueryHandler_withFilter_shouldBeOk() {
        // Given
        final QueryHandler<Query,QueryResult> queryHandler = new QueryHandlerWithFiltersForTest();

        final KasperQueryGateway queryGateway = new KasperQueryGateway();
        queryGateway.register(new QueryFilterInterceptorFactory());
        queryGateway.register(queryHandler);

        // When
        final Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> interceptorChain =
                queryGateway.getInterceptorChain(Query.class);

        // Then
        assertTrue(interceptorChain.isPresent());
        assertEquals(InterceptorA.class, interceptorChain.get().actor.get().getClass());
        assertEquals(QueryHandlerInterceptor.class, interceptorChain.get().next.get().actor.get().getClass());
    }

    @Test
    public void getInterceptorChain_withQueryHandler_withCache_shouldBeOk() {
        // Given
        final QueryHandler<Query,QueryResult> queryHandler = new QueryHandlerWithCacheForTest();

        final KasperQueryGateway queryGateway = new KasperQueryGateway();
        queryGateway.register(new CacheInterceptorFactory());
        queryGateway.register(queryHandler);

        // When
        final Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> interceptorChain =
                queryGateway.getInterceptorChain(Query.class);

        // Then
        assertTrue(interceptorChain.isPresent());
        assertEquals(CacheInterceptor.class, interceptorChain.get().actor.get().getClass());
        assertEquals(QueryHandlerInterceptor.class, interceptorChain.get().next.get().actor.get().getClass());
    }

}
