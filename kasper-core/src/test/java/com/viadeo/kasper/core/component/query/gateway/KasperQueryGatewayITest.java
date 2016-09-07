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