// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.cqrs.query;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.core.locators.impl.DefaultQueryServicesLocator;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.impl.DefaultQueryGateway;
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
    private class TestPayload implements QueryPayload {
        public int state = STATE_START;
    }

    @XKasperUnregistered
    private class TestService implements QueryService<TestQuery, TestPayload> {
        @Override
        public QueryResult<TestPayload> retrieve(final QueryMessage message) throws Exception {
            return new QueryResult<>(new TestPayload());
        }
    }

    @XKasperUnregistered
    private class TestFilter implements QueryFilter<TestQuery>, ResultFilter<TestPayload> {
        @Override
        public QueryResult<TestPayload> filter(final Context context, final QueryResult<TestPayload> result) {
            result.getPayload().state = STATE_MODIFIED;
            return result;
        }

        @Override
        public TestQuery filter(Context context, TestQuery query) {
            query.state = STATE_MODIFIED;
            return query;
        }
    }

    @XKasperUnregistered
    private class TestFilterGlobal implements QueryFilter<Query> {
        @Override
        public Query filter(final Context context, final Query query) throws KasperQueryException {
            return query;
        }
    }

    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked") // Safe
    public void queryFilterShouldBeCalled() throws Exception {

        // Given
        final TestService service = spy(new TestService());
        final TestFilter filter = spy(new TestFilter());
        final TestFilterGlobal filterGlobal = spy(new TestFilterGlobal());
        final QueryServicesLocator locator = new DefaultQueryServicesLocator();

        locator.registerService("testService", service, TestDomain.class);
        locator.registerFilter("testFilter", filter);
        locator.registerFilter("testFilter2", filterGlobal, true);
        locator.registerFilterForService(service.getClass(), filter.getClass());

        final DefaultQueryGateway gateway = new DefaultQueryGateway();
        gateway.setQueryServicesLocator(locator);

        final Context context = DefaultContextBuilder.get();
        final TestQuery query = new TestQuery();

        // When
        final QueryResult<TestPayload> queryResult = gateway.retrieve(query, context);

        // Then
        verify(filter).filter(eq(context), any(TestQuery.class));
        assertEquals(STATE_MODIFIED, query.state);

        verify(filter).filter(eq(context), any(QueryResult.class));
        assertEquals(STATE_MODIFIED, queryResult.getPayload().state);

        verify(filterGlobal).filter(eq(context), any(Query.class));
    }

}
