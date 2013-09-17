// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.core.locators.impl.DefaultQueryServicesLocator;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.ddd.Domain;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class QueryServicesProcessorTest {

    private static final String SERVICE_NAME = "TestService";

    // ------------------------------------------------------------------------

    final QueryServicesLocator locator = spy(new DefaultQueryServicesLocator());
    final QueryServicesProcessor processor = new QueryServicesProcessor();

    { processor.setQueryServicesLocator(locator); }

    // ------------------------------------------------------------------------

    @XKasperUnregistered
    public class TestDomain implements Domain { }

    @XKasperUnregistered
    public class TestQuery implements Query { }

    @XKasperUnregistered
    public class TestFilter implements ServiceFilter { }

    @XKasperUnregistered
    public class TestFilter2 implements ServiceFilter { }

    public class TestPayload implements QueryPayload { }

    // ------------------------------------------------------------------------

    @XKasperQueryService( name = SERVICE_NAME, domain = TestDomain.class )
    public class TestService implements QueryService<TestQuery, TestPayload> {
        @Override
        public QueryResult<TestPayload> retrieve(final QueryMessage message) throws Exception { return null; }
    }

    @XKasperQueryService( domain = TestDomain.class )
    public class TestServiceNoName implements QueryService<TestQuery, TestPayload> {
        @Override
        public QueryResult<TestPayload> retrieve(final QueryMessage message) throws Exception { return null; }
    }

    @XKasperQueryService( domain = TestDomain.class, filters = TestFilter.class )
    public class TestServiceOneFilter implements QueryService<TestQuery, TestPayload> {
        @Override
        public QueryResult<TestPayload> retrieve(final QueryMessage message) throws Exception { return null; }
    }

    @XKasperQueryService( domain = TestDomain.class, filters = { TestFilter.class, TestFilter2.class } )
    public class TestServiceMultipleFilters implements QueryService<TestQuery, TestPayload> {
        @Override
        public QueryResult<TestPayload> retrieve(final QueryMessage message) throws Exception { return null; }
    }

    // ------------------------------------------------------------------------

    @Test
    public void processorShouldRegisterServiceWithName() {

        // Given
        final TestService service = new TestService();

        // When
        processor.process(service.getClass(), service);

        // Then
        verify(locator).registerService(eq(SERVICE_NAME), eq(service), eq(TestDomain.class));

    }

    @Test
    public void processorShouldRegisterServiceWithoutName() {

        // Given
        final TestServiceNoName service = new TestServiceNoName();

        // When
        processor.process(service.getClass(), service);

        // Then
        verify(locator).registerService(eq(service.getClass().getSimpleName()), eq(service), eq(TestDomain.class));

    }

    @Test
    public void processorShouldRegisterServiceWithOneFilter() {

         // Given
        final TestServiceOneFilter service = new TestServiceOneFilter();

        // When
        processor.process(service.getClass(), service);

        // Then
        verify(locator).registerService(any(String.class), any(QueryService.class), eq(TestDomain.class));
        verify(locator).registerFilterForService(eq(service.getClass()), eq(TestFilter.class));

    }

    @Test
    public void processorShouldRegisterServiceWithMultipleFilters() {

         // Given
        final TestServiceMultipleFilters service = new TestServiceMultipleFilters();

        // When
        processor.process(service.getClass(), service);

        // Then
        verify(locator).registerService(any(String.class), any(QueryService.class), eq(TestDomain.class));
        verify(locator).registerFilterForService(eq(service.getClass()), eq(TestFilter.class));
        verify(locator).registerFilterForService(eq(service.getClass()), eq(TestFilter2.class));
    }

}
