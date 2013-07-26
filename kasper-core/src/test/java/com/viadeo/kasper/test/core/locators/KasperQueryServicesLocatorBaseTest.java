// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.core.locators;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.locators.impl.DefaultQueryServicesLocator;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@SuppressWarnings("serial")
public class KasperQueryServicesLocatorBaseTest {

	private DefaultQueryServicesLocator locator;

	private static final class TestDTO implements QueryDTO {}

	private static final class TestQuery implements Query {}

	private static class TestService implements QueryService<TestQuery, TestDTO> {
		@Override
		public TestDTO retrieve(final QueryMessage<TestQuery> message) {
			throw new UnsupportedOperationException();
		}
	}

	// ------------------------------------------------------------------------

	@Before
	public void setUp() throws Exception {
		locator = new DefaultQueryServicesLocator();
	}

	@Test
	public void getServicesEmptyByDefault() {
		assertNotNull(locator.getServices());
		assertEquals(locator.getServices().size(), 0);
	}

    @Test
    public void getServiceByClass() {
        assertNotNull(locator.getServiceFromClass(TestService.class));
    }

	@Test
	@SuppressWarnings("rawtypes")
	public void getServiceFromQueryClassEmptyState() {
		final Optional<QueryService> registeredService = locator.getServiceFromQueryClass(TestQuery.class);
		assertFalse(registeredService.isPresent());
	}

	@Test
	public void oneServiceRegistered() {
		final TestService service = mock(TestService.class);
		locator.registerService("test", service);
		assertEquals(locator.getServices().size(), 1);

		@SuppressWarnings("rawtypes")
		final Optional<QueryService> registeredService = locator.getServiceFromQueryClass(TestQuery.class);
		assertTrue(registeredService.isPresent());
		assertSame(service, registeredService.get());
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void registerNullService() {
		thrown.expect(NullPointerException.class);
		locator.registerService("", null);
	}

    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------

    private static class TestFilter implements ServiceFilter { }
    private static class TestFilter2 implements ServiceFilter { }

    @Test
    public void registerServiceFilter() {

        final Collection<ServiceFilter> filters;

        // Given
        final TestFilter filter = mock(TestFilter.class);
        final TestService service = mock(TestService.class);
        final Class<? extends TestService> serviceClass = service.getClass();

        // When
        locator.registerFilter("testFilter", filter);
        locator.registerService("testService", service);
        locator.registerFilteredService(serviceClass, filter.getClass());

        // Then
        assertEquals(1, locator.getFiltersForServiceClass(serviceClass).size());

        // --

        // Given
        final TestFilter2 filter2 = mock(TestFilter2.class);

        // When
        locator.registerFilter("testFilter2", filter2);
        locator.registerFilteredService(serviceClass, filter2.getClass());

        // Then
        assertEquals(2, locator.getFiltersForServiceClass(serviceClass).size());

         // --

        // Given
        final ServiceFilter filterGlobal = mock(ServiceFilter.class);

        // When
        locator.registerFilter("testFilterGlobal", filterGlobal, true);

        // Then
        assertEquals(3, locator.getFiltersForServiceClass(serviceClass).size());

    }

}
