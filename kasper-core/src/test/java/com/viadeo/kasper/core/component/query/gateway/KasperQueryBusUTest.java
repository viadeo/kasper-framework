// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
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
import com.viadeo.kasper.core.component.query.interceptor.QueryHandlerInterceptorFactory;
import com.viadeo.kasper.core.component.query.interceptor.cache.CacheInterceptor;
import com.viadeo.kasper.core.component.query.interceptor.cache.CacheInterceptorFactory;
import com.viadeo.kasper.core.component.query.interceptor.filter.QueryFilterInterceptorFactory;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
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
        final QueryHandler<Query, QueryResult> queryHandler = new MeasuredQueryHandler(new MetricRegistry(), new QueryHandlerWithFiltersForTest());
        final QueryFilterInterceptorFactory interceptorFactory = spy(new QueryFilterInterceptorFactory());

        queryGateway.register(interceptorFactory);
        queryGateway.register(queryHandler);

        // When
        final Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> interceptorChain =
                queryBus.getInterceptorChain(queryHandler);

        // Then
        assertTrue(interceptorChain.isPresent());
        verify(interceptorChainRegistry).create(eq(QueryHandlerWithFiltersForTest.class), any(QueryHandlerInterceptorFactory.class));
    }
}
