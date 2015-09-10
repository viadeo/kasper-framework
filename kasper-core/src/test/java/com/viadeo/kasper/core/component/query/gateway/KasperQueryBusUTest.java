// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query.gateway;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.core.component.query.AutowiredQueryHandler;
import com.viadeo.kasper.core.component.query.MeasuredQueryHandler;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.interceptor.QueryHandlerInterceptor;
import com.viadeo.kasper.core.component.query.interceptor.cache.CacheInterceptor;
import com.viadeo.kasper.core.component.query.interceptor.cache.CacheInterceptorFactory;
import com.viadeo.kasper.core.component.query.interceptor.filter.QueryFilterInterceptorFactory;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import com.viadeo.kasper.core.interceptor.InterceptorFactory;
import com.viadeo.kasper.core.locators.DefaultQueryHandlersLocator;
import org.junit.Before;
import org.junit.Test;

import static com.viadeo.kasper.core.component.query.gateway.KasperQueryGatewayUTest.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class KasperQueryBusUTest {

    private KasperQueryGateway queryGateway;
    private KasperQueryBus queryBus;
    private InterceptorChainRegistry<Query,QueryResponse<QueryResult>> interceptorChainRegistry;

    // ------------------------------------------------------------------------

    @Before
    public void setup() {
        DefaultQueryHandlersLocator queryHandlersLocator = mock(DefaultQueryHandlersLocator.class);
        interceptorChainRegistry = spy(new InterceptorChainRegistry<Query, QueryResponse<QueryResult>>());
        queryBus = new KasperQueryBus(new MetricRegistry(), interceptorChainRegistry);
        queryGateway = new KasperQueryGateway(
                queryBus,
                queryHandlersLocator
        );
    }

    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    public void getInterceptorChain_withUnknownQuery_shouldReturnNoChain() {
        // Given
        QueryHandler queryHandler = mock(QueryHandler.class);
        when(queryHandler.getInputClass()).thenReturn(Query.class);

        // When
        final Optional<InterceptorChain<Query,QueryResponse<QueryResult>>> requestActorChain =
                queryBus.getInterceptorChain(queryHandler);

        // Then
        assertNotNull(requestActorChain);
        assertFalse(requestActorChain.isPresent());
    }

    @Test
    public void getInterceptorChain_withQueryHandler_shouldBeOk() {
        // Given
        final AutowiredQueryHandler<Query,QueryResult> queryHandler = new QueryHandlerWithFiltersForTest();

        queryGateway.register(queryHandler);

        // When
        final Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> interceptorChain =
                queryBus.getInterceptorChain(queryHandler);

        // Then
        assertTrue(interceptorChain.isPresent());
    }

    @Test
    public void getInterceptorChain_withQueryHandler_withFilter_shouldBeOk() {
        // Given
        final AutowiredQueryHandler<Query,QueryResult> queryHandler = new QueryHandlerWithFiltersForTest();

        queryGateway.register(new QueryFilterInterceptorFactory());
        queryGateway.register(queryHandler);

        // When
        final Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> interceptorChain =
                queryBus.getInterceptorChain(queryHandler);

        // Then
        assertTrue(interceptorChain.isPresent());
        assertEquals(InterceptorA.class, interceptorChain.get().actor.get().getClass());
        assertEquals(QueryHandlerInterceptor.class, interceptorChain.get().next.get().actor.get().getClass());
    }

    @Test
    public void getInterceptorChain_withQueryHandler_withCache_shouldBeOk() {
        // Given
        final AutowiredQueryHandler<Query,QueryResult> queryHandler = new QueryHandlerWithCacheForTest();

        queryGateway.register(new CacheInterceptorFactory());
        queryGateway.register(queryHandler);

        // When
        final Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> interceptorChain =
                queryBus.getInterceptorChain(queryHandler);

        // Then
        assertTrue(interceptorChain.isPresent());
        assertEquals(CacheInterceptor.class, interceptorChain.get().actor.get().getClass());
        assertEquals(QueryHandlerInterceptor.class, interceptorChain.get().next.get().actor.get().getClass());
    }

    @Test
    public void getInterceptorChain_should_create_interceptor_from_the_handler_class() {
        // Given
        final QueryHandler queryHandler = new MeasuredQueryHandler(new MetricRegistry(), new QueryHandlerWithFiltersForTest());
        final QueryFilterInterceptorFactory interceptorFactory = spy(new QueryFilterInterceptorFactory());

        queryGateway.register(interceptorFactory);
        queryGateway.register(queryHandler);

        // When
        final Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> interceptorChain =
                queryBus.getInterceptorChain(queryHandler);

        // Then
        assertTrue(interceptorChain.isPresent());
        verify(interceptorChainRegistry).create(eq(QueryHandlerWithFiltersForTest.class), any(InterceptorFactory.class));
    }
}
