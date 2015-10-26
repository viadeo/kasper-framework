package com.viadeo.kasper.core.component.query.gateway;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.api.annotation.XKasperQuery;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.component.query.BaseQueryHandler;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.core.component.query.interceptor.validation.QueryValidationInterceptorFactory;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import com.viadeo.kasper.core.interceptor.InterceptorFactory;
import com.viadeo.kasper.core.locators.DefaultQueryHandlersLocator;
import com.viadeo.kasper.core.locators.QueryHandlersLocator;
import org.axonframework.commandhandling.interceptors.JSR303ViolationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.validation.constraints.NotNull;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class KasperQueryGatewayITest {

    @XKasperQuery
    private static class TestQuery implements Query {

        @NotNull
        private String value;

        private TestQuery(String value) {
            this.value = value;
        }
    }

    @XKasperUnregistered
    @XKasperQueryHandler(domain = Domain.class)
    private static class TestQueryHandler extends BaseQueryHandler<TestQuery, QueryResult> {
        public QueryResponse<QueryResult> handle(Context context, TestQuery query) {
            return QueryResponse.of(mock(QueryResult.class));
        }
    }

    private KasperQueryGateway queryGateway;
    private QueryHandlersLocator queryHandlersLocator;
    private InterceptorChainRegistry<Query, QueryResponse<QueryResult>> interceptorChainRegistry;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        queryHandlersLocator = spy(new DefaultQueryHandlersLocator());
        interceptorChainRegistry = spy(new InterceptorChainRegistry<Query, QueryResponse<QueryResult>>());

        queryGateway = new KasperQueryGateway(new KasperQueryBus(new MetricRegistry(), interceptorChainRegistry), queryHandlersLocator);
        queryGateway.register(new QueryValidationInterceptorFactory());
    }

    @Test
    public void testRegister() throws Exception {
        // Given
        final TestQueryHandler queryHandler = new TestQueryHandler();

        //when
        queryGateway.register(queryHandler);

        //then
        verify(queryHandlersLocator).registerHandler("TestQueryHandler", queryHandler, Domain.class);
        verify(interceptorChainRegistry).create(eq(queryHandler.getHandlerClass()), any(InterceptorFactory.class));

    }

    @Test
    public void retrieve_is_ok() throws Exception {
        // Given
        queryGateway.register(new TestQueryHandler());

        // When
        QueryResponse<QueryResult> response = queryGateway.retrieve(new TestQuery(""), Contexts.empty());

        // Then
        assertNotNull(response);
        assertTrue(response.isOK());
    }

    @Test
    public void retrieve_from_an_invalid_query_throws_exception() throws Exception {
        // Given
        queryGateway.register(new TestQueryHandler());

        // Then
        exception.expect(JSR303ViolationException.class);

        // When
        QueryResponse<QueryResult> response = queryGateway.retrieve(new TestQuery(null), Contexts.empty());

        assertNull(response);
    }

    @Test
    public void retrieve_for_future_is_ok() throws Exception {
        // Given
        queryGateway.register(new TestQueryHandler());

        // When
        Future<QueryResponse<QueryResult>> future = queryGateway.retrieveForFuture(new TestQuery(""), Contexts.empty());
        QueryResponse<QueryResult> response = future.get();

        // Then
        assertNotNull(response);
        assertTrue(response.isOK());
    }

    @Test
    public void retrieve_for_future_from_an_invalid_query_throws_exception() throws Exception {
        try {
            // Given
            queryGateway.register(new TestQueryHandler());

            // When
            Future<QueryResponse<QueryResult>> future = queryGateway.retrieveForFuture(new TestQuery(null), Contexts.empty());
            future.get();

            fail();
        } catch (final Exception e) {
            // Then
            assertTrue(ExecutionException.class.isAssignableFrom(e.getClass()));
            assertTrue(JSR303ViolationException.class.isAssignableFrom(e.getCause().getClass()));
        }

    }
}