// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.api.annotation.XKasperDomain;
import com.viadeo.kasper.api.annotation.XKasperQuery;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.core.component.MeasuredHandler;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class MeasuredQueryHandlerUTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void propagate_a_caught_runtime_exception() {
        // Given
        MetricRegistry metricRegistry = spy(new MetricRegistry());

        QueryHandler<TestQuery,QueryResult> handler = spy(new TestQueryHandler());
        doThrow(new RuntimeException("bazinga!")).when(handler).handle(any(QueryMessage.class));

        MeasuredHandler measuredHandler = new MeasuredQueryHandler(metricRegistry, handler);

        // Then
        exception.expect(RuntimeException.class);
        exception.expectMessage("bazinga!");

        // When
        measuredHandler.handle(new QueryMessage(Contexts.empty(), mock(Query.class)));
    }

    @XKasperUnregistered
    @XKasperDomain(prefix = "test", label = "test")
    public static class TestDomain implements Domain { }

    @XKasperUnregistered
    @XKasperQuery
    private static class TestQuery implements Query {}

    @XKasperUnregistered
    @XKasperQueryHandler(domain = TestDomain.class)
    private static class TestQueryHandler extends AutowiredQueryHandler<TestQuery, QueryResult> {

        @Override
        public QueryResponse handle(Context context, TestQuery event) {
            return QueryResponse.of(mock(QueryResult.class));
        }
    }
}
