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

public class QueryFilterITest {

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
    private class TestFilterQuery implements QueryFilter<TestQuery> {
        @Override
        public TestQuery adapt(Context context, TestQuery query) {
            query.state = STATE_MODIFIED;
            return query;
        }

        @Override
        public String getName() {
            return getClass().getSimpleName();
        }
    }

    @XKasperUnregistered
    private class TestQueryResponse implements QueryResponseFilter<TestResult> {
        @Override
        public QueryResponse<TestResult> adapt(Context context, QueryResponse<TestResult> response) {
            response.getResult().state = STATE_MODIFIED;
            return response;
        }

        @Override
        public String getName() {
            return getClass().getSimpleName();
        }
    }

    @XKasperUnregistered
    private class TestFilterGlobal implements QueryFilter<Query> {
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
    public void queryFilterShouldBeCalled() throws Exception {

        // Given
        KasperMetrics.setMetricRegistry(new MetricRegistry());

        final TestHandler service = spy(new TestHandler());
        final TestFilterQuery filter = spy(new TestFilterQuery());
        final TestQueryResponse filterReponse = spy(new TestQueryResponse());
        final TestFilterGlobal filterGlobal = spy(new TestFilterGlobal());
        final DomainResolver domainResolver = new DomainResolver();
        final QueryHandlerResolver queryHandlerResolver = new QueryHandlerResolver();
        queryHandlerResolver.setDomainResolver(domainResolver);

        final DefaultQueryHandlersLocator locator = new DefaultQueryHandlersLocator(queryHandlerResolver);
        locator.registerHandler("testService", service, TestDomain.class);
        locator.registerFilter("testFilter", filter);
        locator.registerFilter("testFilter1", filterReponse);
        locator.registerFilter("testFilter2", filterGlobal, true);
        locator.registerFilterForQueryHandler(service.getClass(), filter.getClass());
        locator.registerFilterForQueryHandler(service.getClass(), filterReponse.getClass());

        final KasperQueryGateway gateway = new KasperQueryGateway(locator);

        final Context context = DefaultContextBuilder.get();
        final TestQuery query = new TestQuery();

        // When
        final QueryResponse<TestResult> queryResponse = gateway.retrieve(query, context);

        // Then
        assertEquals(STATE_MODIFIED, query.state);
        assertEquals(STATE_MODIFIED, queryResponse.getResult().state);

        verify(filterGlobal).adapt(eq(context), any(Query.class));
    }

}
