// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.google.common.collect.Lists;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResult;
import org.junit.Test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpQueryExposerUTest {

    public static class AQuery implements Query {
        private static final long serialVersionUID = -4789744274328803972L;
    }

    public static class QueryHandlerA extends QueryHandler<AQuery, QueryResult> { }

    public static class QueryHandlerB extends QueryHandler<AQuery, QueryResult> { }

    @Test(expected = HttpExposerError.class)
    public void init_withTwoEventListeners_listeningTheSameEvent_throwException() throws Exception {
        // Given
        final List<ExposureDescriptor<Query, QueryHandler>> descriptors = Lists.newArrayList(
                new ExposureDescriptor<Query, QueryHandler>(AQuery.class, QueryHandlerA.class),
                new ExposureDescriptor<Query, QueryHandler>(AQuery.class, QueryHandlerB.class)
        );

        final ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getContextPath()).thenReturn("");

        final ServletConfig servletConfig = mock(ServletConfig.class);
        when(servletConfig.getServletContext()).thenReturn(servletContext);

        final HttpQueryExposer queryExposer = new HttpQueryExposer(mock(QueryGateway.class), descriptors);

        // When
        queryExposer.init(servletConfig);

        // Then throw an exception
    }
}
