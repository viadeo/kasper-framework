// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.cqrs.query;


import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.core.locators.impl.DefaultQueryServicesLocator;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.impl.DefaultQueryGateway;
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

    private class TestQuery implements Query {
        public int state = STATE_START;
    }

    private class TestDTO implements QueryDTO {
        public int state = STATE_START;
    }

    private class TestService implements QueryService<TestQuery, TestDTO> {
        @Override
        public TestDTO retrieve(final QueryMessage message) throws Exception {
            return new TestDTO();
        }
    }

    private class TestFilter implements QueryFilter, DTOFilter {
        @Override
        public void filter(Context context, Query query) throws KasperQueryException {
            ((TestQuery) query).state = STATE_MODIFIED;
        }

        @Override
        public void filter(Context context, QueryDTO dto) throws KasperQueryException {
            ((TestDTO) dto).state = STATE_MODIFIED;
        }
    }

    // ------------------------------------------------------------------------

    @Test
    public void queryFilterShouldBeCalled() throws Exception {

        // Given
        final TestService service = spy(new TestService());
        final TestFilter filter = spy(new TestFilter());
        final QueryServicesLocator locator = new DefaultQueryServicesLocator();

        locator.registerService("testService", service);
        locator.registerFilter("testFilter", filter);
        locator.registerFilteredService(service.getClass(), filter.getClass());

        final DefaultQueryGateway gateway = new DefaultQueryGateway();
        gateway.setQueryServicesLocator(locator);

        final Context context = DefaultContextBuilder.get();
        final TestQuery query = new TestQuery();

        // When
        final TestDTO dto = gateway.retrieve(query, context);

        // Then
        verify(filter).filter(eq(context), any(Query.class));
        assertEquals(STATE_MODIFIED, query.state);

        verify(filter).filter(eq(context), any(QueryDTO.class));
        assertEquals(STATE_MODIFIED, dto.state);
    }

}
