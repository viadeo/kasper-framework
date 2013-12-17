// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.locators.impl.DefaultQueryHandlersLocator;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.core.resolvers.DomainResolver;
import com.viadeo.kasper.core.resolvers.QueryHandlerResolver;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.impl.KasperQueryGateway;
import com.viadeo.kasper.ddd.Domain;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class QueryAdapterITest {

    private static int STATE_START = 0;
    private static int STATE_MODIFIED = 42;

    // ------------------------------------------------------------------------

    @XKasperUnregistered
    private class TestDomain implements Domain { }

    @XKasperUnregistered
    private class TestQuery implements Query {
        public int state = STATE_START;
    }

    @XKasperUnregistered
    private class TestResult implements QueryResult {
        public int state = STATE_START;
    }

    @XKasperUnregistered
    private class TestHandler extends QueryHandler<TestQuery, TestResult> {
        @Override
        public QueryResponse<TestResult> retrieve(final TestQuery query) throws Exception {
            return QueryResponse.of(new TestResult());
        }
    }

    @XKasperUnregistered
    private class TestAdapterQuery implements QueryAdapter<TestQuery> {
        @Override
        public TestQuery adapt(final Context context, final TestQuery query) {
            query.state = STATE_MODIFIED;
            return query;
        }

        @Override
        public String getName() {
            return getClass().getSimpleName();
        }
    }

    @XKasperUnregistered
    private class TestQueryResponse implements QueryResponseAdapter<TestResult> {
        @Override
        public QueryResponse<TestResult> adapt(final Context context, final QueryResponse<TestResult> response) {
            response.getResult().state = STATE_MODIFIED;
            return response;
        }

        @Override
        public String getName() {
            return getClass().getSimpleName();
        }
    }

    @XKasperUnregistered
    private class TestAdapterGlobal implements QueryAdapter<Query> {
        @Override
        public Query adapt(final Context context, final Query query) throws KasperQueryException {
            return query;
        }

        @Override
        public String getName() {
            return getClass().getSimpleName();
        }
    }

    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked") // Safe
    public void queryAdapterShouldBeCalled() throws Exception {

        // Given
        KasperMetrics.setMetricRegistry(new MetricRegistry());

        final TestHandler service = spy(new TestHandler());
        final TestAdapterQuery adapter = spy(new TestAdapterQuery());
        final TestQueryResponse responseAdapter = spy(new TestQueryResponse());
        final TestAdapterGlobal globalAdapter = spy(new TestAdapterGlobal());
        final DomainResolver domainResolver = new DomainResolver();
        final QueryHandlerResolver queryHandlerResolver = new QueryHandlerResolver();
        queryHandlerResolver.setDomainResolver(domainResolver);

        final DefaultQueryHandlersLocator locator = new DefaultQueryHandlersLocator(queryHandlerResolver);
        locator.registerHandler("testService", service, TestDomain.class);
        locator.registerAdapter("testAdapter", adapter);
        locator.registerAdapter("testAdapter1", responseAdapter);
        locator.registerAdapter("testAdapter2", globalAdapter, true);
        locator.registerAdapterForQueryHandler(service.getClass(), adapter.getClass());
        locator.registerAdapterForQueryHandler(service.getClass(), responseAdapter.getClass());

        final KasperQueryGateway gateway = new KasperQueryGateway(locator);

        final Context context = DefaultContextBuilder.get();
        final TestQuery query = new TestQuery();

        // When
        final QueryResponse<TestResult> queryResponse = gateway.retrieve(query, context);

        // Then
        assertEquals(STATE_MODIFIED, query.state);
        assertEquals(STATE_MODIFIED, queryResponse.getResult().state);

        verify(globalAdapter).adapt(eq(context), any(Query.class));
    }

}
