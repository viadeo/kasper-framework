// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.core.locators.impl.DefaultQueryServicesLocator;
import com.viadeo.kasper.cqrs.query.ServiceFilter;
import com.viadeo.kasper.cqrs.query.annotation.XKasperServiceFilter;
import com.viadeo.kasper.ddd.Domain;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class QueryFiltersProcessorTest {

    private static final String FILTER_NAME = "TestFilter";

    // ------------------------------------------------------------------------

    final QueryServicesLocator locator = spy(new DefaultQueryServicesLocator());
    final ServiceFiltersProcessor processor = new ServiceFiltersProcessor();
    { processor.setQueryServicesLocator(locator); }

    // ------------------------------------------------------------------------

    @XKasperServiceFilter( name = FILTER_NAME )
    private static final class TestFilterSimple implements ServiceFilter {
        TestFilterSimple() {}
    }

    @XKasperServiceFilter
    private static final class TestFilterNoName implements ServiceFilter {
        TestFilterNoName() {}
    }

    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    public void processorShouldRegisterFilterWithName() {

        // Given
        final TestFilterSimple filter = new TestFilterSimple();

        // When
        processor.process(filter.getClass(), filter);

        // Then
        verify(locator).registerFilter(eq(FILTER_NAME), eq(filter), eq(false), (Class<? extends Domain>) isNull());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void processorShouldRegisterFilterWithoutName() {

        // Given
        final TestFilterNoName filter = new TestFilterNoName();

        // When
        processor.process(filter.getClass(), filter);

        // Then
        verify(locator).registerFilter(eq(filter.getClass().getSimpleName()), eq(filter), eq(false), (Class<? extends Domain>) isNull());
    }

}
