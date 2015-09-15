// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.resilience;

import com.codahale.metrics.MetricRegistry;
import com.netflix.hystrix.HystrixCircuitBreaker;
import com.netflix.hystrix.HystrixCommandKey;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Contexts;
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

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
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

    // ------------------------------------------------------------------------

    @Before
    public void setUp() throws Exception {
        queryHandler = mockedQueryHandler();

        configurer = mock(ResilienceConfigurator.class);
        when(configurer.configure(any())).thenReturn(new ResilienceConfigurator.InputConfig(true, 20, 40, 40000, 1000));

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

        final HystrixCircuitBreaker circuitBreaker = HystrixCircuitBreaker.Factory.getInstance(
                HystrixCommandKey.Factory.asKey(Query.class.getName())
        );

        if (circuitBreaker != null) {
            circuitBreaker.markSuccess();
        }
    }


    @Test
    public void proceed_interception_with_circuit_breaker_open() throws Exception {
        // Given
        when(configurer.configure(any())).thenReturn(new ResilienceConfigurator.InputConfig(true, 2, 40, 500, 1000));
        doThrow(new NullPointerException("fake")).when(queryHandler).handle(anyQueryMessage());

        // When
        for (int i = 0; i <2; i++) {
            final QueryResponse<QueryResult> response = interceptorChain.next(new Query(), Contexts.empty());
            assertNotNull(response);
            assertEquals(KasperResponse.Status.FAILURE, response.getStatus());
            assertTrue(response.getReason().hasMessage(String.format("Failed to execute request, <handler=%s>", QueryHandler.class.getName())));
        }
        Thread.sleep(500);

        final QueryResponse<QueryResult> response = interceptorChain.next(new Query(), Contexts.empty());

        // Then
        assertNotNull(response);
        assertEquals(KasperResponse.Status.FAILURE, response.getStatus());
        assertTrue(response.getReason().hasMessage(String.format("Circuit-breaker is open, <handler=%s>", QueryHandler.class.getName())));
    }

    @Test
    public void proceed_interception_after_to_have_close_the_circuit() throws Exception {
        // Given
        when(configurer.configure(any())).thenReturn(new ResilienceConfigurator.InputConfig(true, 2, 40, 100, 1000));
        when(queryHandler.handle(anyQueryMessage()))
                .thenThrow(new NullPointerException("fake1"))
                .thenThrow(new NullPointerException("fake2"))
                .thenReturn(QueryResponse.of(mock(QueryResult.class)));

        // When
        for (int i = 0; i <2; i++) {
            final QueryResponse<QueryResult> response = interceptorChain.next(new Query(), Contexts.empty());
            assertNotNull(response);
            assertEquals(KasperResponse.Status.FAILURE, response.getStatus());
            assertTrue(response.getReason().hasMessage(String.format("Failed to execute request, <handler=%s>", QueryHandler.class.getName())));
        }
        Thread.sleep(500);

        QueryResponse<QueryResult> response = interceptorChain.next(new Query(), Contexts.empty());

        // Then
        assertNotNull(response);
        assertEquals(KasperResponse.Status.FAILURE, response.getStatus());
        assertTrue(response.getReason().hasMessage(String.format("Circuit-breaker is open, <handler=%s>", QueryHandler.class.getName())));

        Thread.sleep(500);

        response = interceptorChain.next(new Query(), Contexts.empty());
        assertEquals(KasperResponse.Status.OK, response.getStatus());
    }

}
