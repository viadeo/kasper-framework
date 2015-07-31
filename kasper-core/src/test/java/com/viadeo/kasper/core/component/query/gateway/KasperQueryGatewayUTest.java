// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query.gateway;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.query.AutowiredQueryHandler;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryCache;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryFilter;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.core.component.query.interceptor.QueryHandlerInterceptor;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptor;
import com.viadeo.kasper.core.component.query.interceptor.cache.CacheInterceptor;
import com.viadeo.kasper.core.component.query.interceptor.cache.CacheInterceptorFactory;
import com.viadeo.kasper.core.component.query.interceptor.filter.QueryFilterInterceptorFactory;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import com.viadeo.kasper.core.locators.DefaultQueryHandlersLocator;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;

public class KasperQueryGatewayUTest {

    private final KasperQueryGateway queryGateway;
    private final DefaultQueryHandlersLocator queryHandlersLocator;
    private final InterceptorChainRegistry interceptorChainRegistry;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // ------------------------------------------------------------------------

    @XKasperQueryHandler(domain = Domain.class)
    private static class QueryHandlerForTest extends AutowiredQueryHandler<Query, QueryResult> { }

    @XKasperQueryHandler(domain = Domain.class)
    @XKasperQueryFilter(value = {InterceptorA.class})
    private static class QueryHandlerWithFiltersForTest extends AutowiredQueryHandler<Query, QueryResult> { }

    @XKasperQueryHandler(domain = Domain.class, cache = @XKasperQueryCache(enabled = true))
    private static class QueryHandlerWithCacheForTest extends AutowiredQueryHandler<Query, QueryResult> { }

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
        final AutowiredQueryHandler queryHandler = null;

        // When
        queryGateway.register(queryHandler);

        // Then throws an exception
    }

    @Test
    public void register_withQueryHandler_shouldBeRegistered() {
        // Given
        final AutowiredQueryHandler queryHandler = new QueryHandlerForTest();

        // When
        queryGateway.register(queryHandler);

        // Then
        verify(queryHandlersLocator).registerHandler(refEq("QueryHandlerForTest"), refEq(queryHandler), refEq(Domain.class));
        verifyNoMoreInteractions(queryHandlersLocator);
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
        final AutowiredQueryHandler<Query,QueryResult> queryHandler = new QueryHandlerWithFiltersForTest();

        final KasperQueryGateway queryGateway = new KasperQueryGateway(new MetricRegistry());
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
        final AutowiredQueryHandler<Query,QueryResult> queryHandler = new QueryHandlerWithFiltersForTest();

        final KasperQueryGateway queryGateway = new KasperQueryGateway(new MetricRegistry());
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
        final AutowiredQueryHandler<Query,QueryResult> queryHandler = new QueryHandlerWithCacheForTest();

        final KasperQueryGateway queryGateway = new KasperQueryGateway(new MetricRegistry());
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
