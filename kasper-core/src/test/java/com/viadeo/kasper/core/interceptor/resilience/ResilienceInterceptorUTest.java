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
import com.netflix.hystrix.HystrixCircuitBreaker;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.exception.KasperSecurityException;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperResponse;
import com.viadeo.kasper.core.TestDomain;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.QueryMessage;
import com.viadeo.kasper.core.component.query.interceptor.QueryHandlerInterceptorFactory;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ResilienceInterceptorUTest {

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

    private static class SlowAnswer implements Answer<Void> {

        private int sleepInMs = 10000; // default

        public SlowAnswer(final int sleepInMs) {
            this.sleepInMs = sleepInMs;
        }

        @Override
        public Void answer(final InvocationOnMock invocation) {
            if (sleepInMs > 0) {
                try {
                    Thread.sleep(sleepInMs);
                } catch (final InterruptedException e) {
                    // interrupted
                }
            }
            return null;
        }
    }

    // ------------------------------------------------------------------------

    @Before
    public void setUp() throws Exception {
        ResilienceInterceptor.metricInitialized = true;

        queryHandler = mockedQueryHandler();

        configurer = mock(ResilienceConfigurator.class);
        when(configurer.configure(any(Query.class))).thenReturn(new ResilienceConfigurator.InputConfig(true, 20, 40, 40000, 1000, 10, 5));
        when(configurer.configure(any(Query2.class))).thenReturn(new ResilienceConfigurator.InputConfig(true, 20, 40, 40000, 1000, 10, 5));
        when(configurer.isHystrixMetricEnable()).thenReturn(Boolean.TRUE);

        final MetricRegistry metricRegistry = new MetricRegistry();
        final InterceptorChainRegistry<com.viadeo.kasper.api.component.query.Query, QueryResponse<QueryResult>> chainRegistry = new InterceptorChainRegistry<>();

        chainRegistry.register(ResilienceInterceptorFactories.forQuery(
                metricRegistry,
                new ResiliencePolicy(),
                configurer
        ));
        interceptorChain = chainRegistry.create(
                QueryHandler.class,
                new QueryHandlerInterceptorFactory(queryHandler)
        ).get();

        KasperMetrics.setMetricRegistry(metricRegistry);
    }

    @After
    public void tearDown() throws Exception {
        for (Class aClass : new Class[] {Query.class/*, Query2.class*/}) {
            final HystrixCircuitBreaker circuitBreaker = HystrixCircuitBreaker.Factory.getInstance(
                    HystrixCommandKey.Factory.asKey(aClass.getName())
            );

            if (circuitBreaker != null) {
                circuitBreaker.markSuccess();
            }
        }
    }

    @Test
    public void proceed_interception_with_an_handler_returning_an_ok_as_response() throws Exception {
        // Given
        when(queryHandler.handle(anyQueryMessage())).thenReturn(QueryResponse.<QueryResult>of(
                new TestDomain.TestQueryResult())
        );

        // When
        final QueryResponse response = interceptorChain.next(new Query(), Contexts.empty());

        // Then
        assertNotNull(response);
        assertEquals(KasperResponse.Status.OK, response.getStatus());
    }

    @Test
    public void proceed_interception_with_an_handler_returning_an_error_as_response() throws Exception {
        // Given
        when(queryHandler.handle(anyQueryMessage())).thenReturn(QueryResponse.error(CoreReasonCode.UNKNOWN_REASON));

        // When
        final QueryResponse response = interceptorChain.next(new Query(), Contexts.empty());

        // Then
        assertNotNull(response);
        assertEquals(KasperResponse.Status.ERROR, response.getStatus());
    }

    @Test
    public void proceed_interception_with_an_handler_throwing_an_exception() throws Exception {
        // Given
        doThrow(new NullPointerException("fake")).when(queryHandler).handle(anyQueryMessage());

        // When
        final QueryResponse<QueryResult> response = interceptorChain.next(new Query(), Contexts.empty());

        // Then
        assertNotNull(response);
        assertEquals(KasperResponse.Status.FAILURE, response.getStatus());
        assertEquals(CoreReasonCode.INTERNAL_COMPONENT_ERROR, response.getReason().getCoreReasonCode());
        assertTrue(response.getReason().hasMessage(String.format("Failed to execute request, <handler=%s>", QueryHandler.class.getName())));
    }

    @Test
    public void proceed_interception_with_an_handler_throwing_an_exception_representing_an_error() throws Exception {
        // Given
        doThrow(new KasperSecurityException("fake security", CoreReasonCode.UNKNOWN_REASON)).when(queryHandler).handle(anyQueryMessage());

        // Then
        exception.expect(RuntimeException.class);
        exception.expectMessage("fake security");

        // When
        interceptorChain.next(new Query(), Contexts.empty());
    }

    @Test
    public void proceed_interception_with_an_handler_falling_in_timeout() throws Exception {
        // Given
        doAnswer(new SlowAnswer(1500)).when(queryHandler).handle(anyQueryMessage());

        // When
        final QueryResponse<QueryResult> response = interceptorChain.next(new Query(), Contexts.empty());

        // Then
        assertNotNull(response);
        assertEquals(KasperResponse.Status.FAILURE, response.getStatus());
        assertEquals(CoreReasonCode.INTERNAL_COMPONENT_TIMEOUT, response.getReason().getCoreReasonCode());
        assertTrue(response.getReason().hasMessage(String.format("Error executing request due to a timed out, <handler=%s>", QueryHandler.class.getName())));
    }

    @Test
    public void proceed_interception_with_circuit_breaker_open() throws Exception {
        // Given
        doThrow(new NullPointerException("fake")).when(queryHandler).handle(anyQueryMessage());
        // we tweak the configuration in order to open the circuit breaker fo a specific input
        when(configurer.configure(any(Query2.class))).thenReturn(new ResilienceConfigurator.InputConfig(true, 0, 0, 100, 1000, 10, 5));

        // When
        final QueryResponse<QueryResult> response = interceptorChain.next(new Query2(), Contexts.empty());

        // Then
        assertNotNull(response);
        assertEquals(KasperResponse.Status.FAILURE, response.getStatus());
        assertEquals(CoreReasonCode.SERVICE_UNAVAILABLE, response.getReason().getCoreReasonCode());
        assertTrue(response.getReason().hasMessage(String.format("Circuit-breaker is open, <handler=%s>", QueryHandler.class.getName())));
    }

    @Test
    public void register_a_metrics_publisher_on_hystrix_is_ok() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        // Given
        ResilienceInterceptor.metricInitialized = false;

        final Constructor<?> constructor = HystrixPlugins.class.getDeclaredConstructor();
        constructor.setAccessible(Boolean.TRUE);
        final HystrixPlugins hp = (HystrixPlugins) constructor.newInstance();
        final HystrixPlugins hystrixPlugins = spy(hp);

        // When
        ResilienceInterceptor.registerMetricsPublisherOnHystrix(new MetricRegistry(), hystrixPlugins, configurer);

        // Then
        verify(hystrixPlugins, atMost(1)).registerMetricsPublisher(any(HystrixMetricsPublisher.class));
    }

    @Test
    public void do_not_register_a_metrics_publisher_on_hystrix_when_it_is_disabled() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        // Given
        ResilienceInterceptor.metricInitialized = false;

        ResilienceConfigurator configurator = mock(ResilienceConfigurator.class);
        when(configurator.isHystrixMetricEnable()).thenReturn(Boolean.FALSE);

        final Constructor<?> constructor = HystrixPlugins.class.getDeclaredConstructor();
        constructor.setAccessible(Boolean.TRUE);
        final HystrixPlugins hp = (HystrixPlugins) constructor.newInstance();
        final HystrixPlugins hystrixPlugins = spy(hp);

        // When
        ResilienceInterceptor.registerMetricsPublisherOnHystrix(new MetricRegistry(), hystrixPlugins, configurator);

        // Then
        verify(hystrixPlugins, never()).registerMetricsPublisher(any(HystrixMetricsPublisher.class));
    }

}
