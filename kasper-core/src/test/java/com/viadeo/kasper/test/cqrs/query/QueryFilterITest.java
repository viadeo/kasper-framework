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
    private class TestResult {
        public int state = STATE_START;
    }

    @XKasperUnregistered
    private class TestService implements QueryService<TestQuery, TestResult> {
        @Override
        public QueryResult<TestResult> retrieve(final QueryMessage message) throws Exception {
            return new QueryResult<TestResult>(new TestResult());
        }
    }

    @XKasperUnregistered
    private class TestFilter implements QueryFilter, ResultFilter {
        @Override
        public void filter(final Context context, final Query query) throws KasperQueryException {
            ((TestQuery) query).state = STATE_MODIFIED;
        }

        @Override
        public void filter(final Context context, final QueryResult result) throws KasperQueryException {
            ((TestResult) result.getResult()).state = STATE_MODIFIED;
        }
    }

    @XKasperUnregistered
    private class TestFilterGlobal implements QueryFilter {
        @Override
        public void filter(final Context context, final Query query) throws KasperQueryException { }
    }

    // ------------------------------------------------------------------------

    @Test
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
        final QueryResult<TestResult> queryResult = gateway.retrieve(query, context);

        // Then
        verify(filter).filter(eq(context), any(Query.class));
        assertEquals(STATE_MODIFIED, query.state);

        verify(filter).filter(eq(context), any(QueryResult.class));
        assertEquals(STATE_MODIFIED, queryResult.getResult().state);

        verify(filterGlobal).filter(eq(context), any(Query.class));
    }

}
