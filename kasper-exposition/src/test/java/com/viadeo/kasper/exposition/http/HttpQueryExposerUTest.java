// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.google.common.collect.Lists;
import com.viadeo.kasper.api.annotation.XKasperUnexposed;
import com.viadeo.kasper.core.component.query.AutowiredQueryHandler;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.exposition.ExposureDescriptor;
import com.viadeo.kasper.platform.Meta;
import com.viadeo.kasper.platform.Platform;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.core.component.query.gateway.QueryGateway;
import com.viadeo.kasper.api.component.query.QueryResult;
import org.junit.Test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpQueryExposerUTest {

    public static class AQuery implements Query {
        private static final long serialVersionUID = -4789744274328803972L;
    }

    public static class QueryHandlerA extends AutowiredQueryHandler<AQuery, QueryResult> { }

    public static class QueryHandlerB extends AutowiredQueryHandler<AQuery, QueryResult> { }

    @XKasperUnexposed
    public static class QueryHandlerC extends AutowiredQueryHandler<AQuery, QueryResult> { }

    @Test(expected = HttpExposerError.class)
    public void init_withTwoHandlers_handlingTheSameQuery_throwException() throws Exception {
        // Given
        final List<ExposureDescriptor<Query, QueryHandler>> descriptors = Lists.newArrayList(
                new ExposureDescriptor<Query, QueryHandler>(AQuery.class, QueryHandlerA.class),
                new ExposureDescriptor<Query, QueryHandler>(AQuery.class, QueryHandlerB.class)
        );

        final ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getContextPath()).thenReturn("");

        final ServletConfig servletConfig = mock(ServletConfig.class);
        when(servletConfig.getServletContext()).thenReturn(servletContext);

        final Platform platform = mock(Platform.class);
        when(platform.getQueryGateway()).thenReturn(mock(QueryGateway.class));
        when(platform.getMeta()).thenReturn(mock(Meta.class));

        final HttpQueryExposer queryExposer = new HttpQueryExposer(platform, descriptors);

        // When
        queryExposer.init(servletConfig);

        // Then throw an exception
    }

    @Test
    public void isExposable_withUnexposedAnnotation_returnFalse() {
        // Given
        final Platform platform = mock(Platform.class);
        when(platform.getQueryGateway()).thenReturn(mock(QueryGateway.class));
        when(platform.getMeta()).thenReturn(mock(Meta.class));

        final HttpQueryExposer exposer = new HttpQueryExposer(
                platform,
                Lists.<ExposureDescriptor<Query, QueryHandler>>newArrayList()
        );

        // When
        boolean exposable = exposer.isExposable(
                new ExposureDescriptor<Query, QueryHandler>(
                        AQuery.class,
                        QueryHandlerC.class
                )
        );

        // Then
        assertFalse(exposable);
    }

    @Test
    public void isExposable_withoutUnexposedAnnotation_returnTrue() {
        // Given
        final Platform platform = mock(Platform.class);
        when(platform.getQueryGateway()).thenReturn(mock(QueryGateway.class));
        when(platform.getMeta()).thenReturn(mock(Meta.class));

        final HttpQueryExposer exposer = new HttpQueryExposer(
                platform,
                Lists.<ExposureDescriptor<Query, QueryHandler>>newArrayList()
        );

        // When
        boolean exposable = exposer.isExposable(
                new ExposureDescriptor<Query, Object>(
                        AQuery.class,
                        QueryHandlerB.class
                )
        );

        // Then
        assertTrue(exposable);
    }
}
