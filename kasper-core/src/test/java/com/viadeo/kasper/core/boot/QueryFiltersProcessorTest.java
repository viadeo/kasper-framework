// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.viadeo.kasper.core.locators.QueryHandlersLocator;
import com.viadeo.kasper.core.locators.impl.DefaultQueryHandlersLocator;
import com.viadeo.kasper.cqrs.query.QueryHandlerAdapter;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandlerAdapter;
import com.viadeo.kasper.ddd.Domain;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class QueryFiltersProcessorTest {

    private static final String FILTER_NAME = "TestAdapter";

    // ------------------------------------------------------------------------

    final QueryHandlersLocator locator = spy(new DefaultQueryHandlersLocator());
    final QueryHandlerAdaptersProcessor processor = new QueryHandlerAdaptersProcessor();
    { processor.setQueryHandlersLocator(locator); }

    // ------------------------------------------------------------------------

    @XKasperQueryHandlerAdapter( name = FILTER_NAME )
    private static final class TestAdapterSimple implements QueryHandlerAdapter {
        TestAdapterSimple() {}
    }

    @XKasperQueryHandlerAdapter
    private static final class TestAdapterNoName implements QueryHandlerAdapter {
        TestAdapterNoName() {}
    }

    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    public void processorShouldRegisterFilterWithName() {

        // Given
        final TestAdapterSimple adapter = new TestAdapterSimple();

        // When
        processor.process(adapter.getClass(), adapter);

        // Then
        verify(locator).registerAdapter(eq(FILTER_NAME), eq(adapter), eq(false), (Class<? extends Domain>) isNull());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void processorShouldRegisterFilterWithoutName() {

        // Given
        final TestAdapterNoName adapter = new TestAdapterNoName();

        // When
        processor.process(adapter.getClass(), adapter);

        // Then
        verify(locator).registerAdapter(eq(adapter.getClass().getSimpleName()), eq(adapter), eq(false), (Class<? extends Domain>) isNull());
    }

}
