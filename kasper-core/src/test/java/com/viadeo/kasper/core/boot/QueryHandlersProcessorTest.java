// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.locators.impl.DefaultQueryHandlersLocator;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryHandlerFilter;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.ddd.Domain;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class QueryHandlersProcessorTest {

    private static final String HANDLER_NAME = "TestHandler";

    // ------------------------------------------------------------------------

    final DefaultQueryHandlersLocator locator = spy(new DefaultQueryHandlersLocator());
    final QueryHandlersProcessor processor = new QueryHandlersProcessor();

    {
        processor.setQueryHandlersLocator(locator);
    }

    // ------------------------------------------------------------------------

    @XKasperUnregistered
    public class TestDomain implements Domain { }

    @XKasperUnregistered
    public class TestQuery implements Query { }

    @XKasperUnregistered
    public class TestFilter implements QueryHandlerFilter { }

    @XKasperUnregistered
    public class TestFilter2 implements QueryHandlerFilter { }

    public class TestResult implements QueryResult { }

    // ------------------------------------------------------------------------

    @XKasperUnregistered
    @XKasperQueryHandler( name = HANDLER_NAME, domain = TestDomain.class )
    public static class TestHandler extends QueryHandler<TestQuery, TestResult> { }

    @XKasperUnregistered
    @XKasperQueryHandler( domain = TestDomain.class )
    public static class TestHandlerNoName extends QueryHandler<TestQuery, TestResult> { }

    @XKasperUnregistered
    @XKasperQueryHandler( domain = TestDomain.class, filters = TestFilter.class )
    public static class TestHandlerOneFilter extends QueryHandler<TestQuery, TestResult> { }

    @XKasperUnregistered
    @XKasperQueryHandler( domain = TestDomain.class, filters = { TestFilter.class, TestFilter2.class } )
    public static class TestHandlerMultipleFilters extends QueryHandler<TestQuery, TestResult> { }

    // ------------------------------------------------------------------------

    @Test
    public void processorShouldRegisterHandlerWithName() {

        // Given
        final TestHandler handler = new TestHandler();

        // When
        processor.process(handler.getClass(), handler);

        // Then
        verify(locator).registerHandler(eq(HANDLER_NAME), eq(handler), eq(TestDomain.class));

    }

    @Test
    public void processorShouldRegisterHandlerWithoutName() {

        // Given
        final TestHandlerNoName handler = new TestHandlerNoName();

        // When
        processor.process(handler.getClass(), handler);

        // Then
        verify(locator).registerHandler(eq(handler.getClass().getSimpleName()), eq(handler), eq(TestDomain.class));

    }

    @Test
    public void processorShouldRegisterHandlerWithOneFilter() {

         // Given
        final TestHandlerOneFilter handler = new TestHandlerOneFilter();

        // When
        processor.process(handler.getClass(), handler);

        // Then
        verify(locator).registerHandler(any(String.class), any(QueryHandler.class), eq(TestDomain.class));
        verify(locator).registerFilterForQueryHandler(eq(handler.getClass()), eq(TestFilter.class));

    }

    @Test
    public void processorShouldRegisterHandlerWithMultipleFilters() {

         // Given
        final TestHandlerMultipleFilters handler = new TestHandlerMultipleFilters();

        // When
        processor.process(handler.getClass(), handler);

        // Then
        verify(locator).registerHandler(any(String.class), any(QueryHandler.class), eq(TestDomain.class));
        verify(locator).registerFilterForQueryHandler(eq(handler.getClass()), eq(TestFilter.class));
        verify(locator).registerFilterForQueryHandler(eq(handler.getClass()), eq(TestFilter2.class));
    }

}
