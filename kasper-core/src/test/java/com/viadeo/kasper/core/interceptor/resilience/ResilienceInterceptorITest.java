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
package com.viadeo.kasper.core.interceptor.resilience;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperResponse;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.QueryMessage;
import com.viadeo.kasper.core.component.query.interceptor.QueryHandlerInterceptorFactory;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ResilienceInterceptorITest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private InterceptorChain<com.viadeo.kasper.api.component.query.Query, QueryResponse<QueryResult>> interceptorChain;

    private QueryHandler<com.viadeo.kasper.api.component.query.Query,QueryResult> queryHandler;
    private ResilienceConfigurator configurer;

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private static QueryMessage<com.viadeo.kasper.api.component.query.Query> anyQueryMessage() {
        return any(QueryMessage.class);
    }

    @SuppressWarnings("unchecked")
    private static QueryHandler<com.viadeo.kasper.api.component.query.Query,QueryResult> mockedQueryHandler() {
        return mock(QueryHandler.class);
    }

    private static class Query implements com.viadeo.kasper.api.component.query.Query { }
    private static class Query2 implements com.viadeo.kasper.api.component.query.Query { }
    private static class Query3 implements com.viadeo.kasper.api.component.query.Query { }

    // ------------------------------------------------------------------------

    @Before
    public void setUp() throws Exception {
        queryHandler = mockedQueryHandler();

        configurer = mock(ResilienceConfigurator.class);

        final InterceptorChainRegistry<com.viadeo.kasper.api.component.query.Query, QueryResponse<QueryResult>> chainRegistry = new InterceptorChainRegistry<>();
        chainRegistry.register(ResilienceInterceptorFactories.forQuery(
                new MetricRegistry(),
                new ResiliencePolicy(),
                configurer
        ));
        interceptorChain = chainRegistry.create(
                QueryHandler.class,
                new QueryHandlerInterceptorFactory(queryHandler)
        ).get();

        KasperMetrics.setMetricRegistry(new MetricRegistry());
    }

    @Test
    public void proceed_interception_without_sufficient_resources() throws Exception {
        // Given
        reset(queryHandler, configurer);
        when(configurer.configure(any(Query2.class))).thenReturn(new ResilienceConfigurator.InputConfig(false, 2, 40, 500, 1000, 1, 1));
        when(queryHandler.handle(anyQueryMessage())).thenAnswer(
                new Answer<QueryResponse>() {
                    @Override
                    public QueryResponse answer(InvocationOnMock invocation) throws Throwable {
                        System.err.println("sleeping..");
                        Thread.sleep(100);
                        return QueryResponse.of(mock(QueryResult.class));
                    }
                }
        );
        int nThreads = 2;

        // When
        CompletionService<QueryResponse<QueryResult>> executor = new ExecutorCompletionService<>(Executors.newFixedThreadPool(nThreads));

        Callable<QueryResponse<QueryResult>> task = new Callable<QueryResponse<QueryResult>>() {
            @Override
            public QueryResponse<QueryResult> call() throws Exception {
                return interceptorChain.next(new Query2(), Contexts.empty());
            }
        };

        for (int i = 0; i < nThreads; i++) {
            executor.submit(task);
        }

        List<QueryResponse<QueryResult>> responseNotOk = Lists.newArrayList();
        for (int i = 0; i < nThreads; i++) {
            QueryResponse<QueryResult> response = executor.take().get();
            if (!response.isOK()) {
                responseNotOk.add(response);
            }
        }

        // Then
        assertTrue(responseNotOk.size() >= 1);
        QueryResponse<QueryResult> response = responseNotOk.get(0);
        assertEquals(KasperResponse.Status.FAILURE, response.getStatus());
        assertEquals(response.getReason().getException().get().getMessage(), CoreReasonCode.SERVICE_UNAVAILABLE, response.getReason().getCoreReasonCode());
        assertTrue(response.getReason().hasMessage(String.format("Rejected request, <handler=%s>", QueryHandler.class.getName())));
    }

    @Test
    public void proceed_interception_with_circuit_breaker_open() throws Exception {
        // Given
        when(configurer.configure(any(Query3.class))).thenReturn(new ResilienceConfigurator.InputConfig(true, 2, 40, 500, 1000, 10, 5));
        doThrow(new NullPointerException("fake")).when(queryHandler).handle(anyQueryMessage());

        // When
        for (int i = 0; i <2; i++) {
            final QueryResponse<QueryResult> response = interceptorChain.next(new Query3(), Contexts.empty());
            assertNotNull(response);
            assertEquals(KasperResponse.Status.FAILURE, response.getStatus());
            assertEquals(CoreReasonCode.INTERNAL_COMPONENT_ERROR, response.getReason().getCoreReasonCode());
            assertTrue(response.getReason().hasMessage(String.format("Failed to execute request, <handler=%s>", QueryHandler.class.getName())));
        }
        Thread.sleep(500);

        final QueryResponse<QueryResult> response = interceptorChain.next(new Query3(), Contexts.empty());

        // Then
        assertNotNull(response);
        assertEquals(KasperResponse.Status.FAILURE, response.getStatus());
        assertEquals(CoreReasonCode.SERVICE_UNAVAILABLE, response.getReason().getCoreReasonCode());
        assertTrue(response.getReason().hasMessage(String.format("Circuit-breaker is open, <handler=%s>", QueryHandler.class.getName())));
    }

    @Test
    public void proceed_interception_after_to_have_close_the_circuit() throws Exception {
        // Given
        when(configurer.configure(any(Query.class))).thenReturn(new ResilienceConfigurator.InputConfig(true, 2, 40, 100, 1000, 10, 5));
        when(queryHandler.handle(anyQueryMessage()))
                .thenThrow(new NullPointerException("fake1"))
                .thenThrow(new NullPointerException("fake2"))
                .thenReturn(QueryResponse.of(mock(QueryResult.class)));

        // When
        for (int i = 0; i <2; i++) {
            final QueryResponse<QueryResult> response = interceptorChain.next(new Query(), Contexts.empty());
            assertNotNull(response);
            assertEquals(KasperResponse.Status.FAILURE, response.getStatus());
            assertEquals(CoreReasonCode.INTERNAL_COMPONENT_ERROR, response.getReason().getCoreReasonCode());
            assertTrue(response.getReason().hasMessage(String.format("Failed to execute request, <handler=%s>", QueryHandler.class.getName())));
        }
        Thread.sleep(500);

        QueryResponse<QueryResult> response = interceptorChain.next(new Query(), Contexts.empty());

        // Then
        assertNotNull(response);
        assertEquals(KasperResponse.Status.FAILURE, response.getStatus());
        assertEquals(CoreReasonCode.SERVICE_UNAVAILABLE, response.getReason().getCoreReasonCode());
        assertTrue(response.getReason().hasMessage(String.format("Circuit-breaker is open, <handler=%s>", QueryHandler.class.getName())));

        Thread.sleep(500);

        response = interceptorChain.next(new Query(), Contexts.empty());
        assertEquals(KasperResponse.Status.OK, response.getStatus());
    }

}
