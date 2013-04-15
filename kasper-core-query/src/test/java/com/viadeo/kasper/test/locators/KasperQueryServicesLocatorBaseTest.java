package com.viadeo.kasper.test.locators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.query.IQuery;
import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.cqrs.query.IQueryMessage;
import com.viadeo.kasper.cqrs.query.IQueryService;
import com.viadeo.kasper.locators.impl.QueryServicesLocatorBase;

@SuppressWarnings("serial")
public class KasperQueryServicesLocatorBaseTest {

	private QueryServicesLocatorBase locator;

	private static final class TestDTO implements IQueryDTO {}

	private static final class TestQuery implements IQuery {}

	private static class TestService implements IQueryService<TestQuery, TestDTO> {
		@Override
		public TestDTO retrieve(final IQueryMessage<TestQuery> message) {
			throw new UnsupportedOperationException();
		}
	}

	// ------------------------------------------------------------------------

	@Before
	public void setUp() throws Exception {
		locator = new QueryServicesLocatorBase();
	}

	@Test
	public void getServices_emptyByDefault() {
		assertNotNull(locator.getServices());
		assertEquals(locator.getServices().size(), 0);
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void getServiceFromQueryClass_emptyState() {
		final Optional<IQueryService> registeredService = locator.getServiceFromQueryClass(TestQuery.class);
		assertFalse(registeredService.isPresent());
	}

	@Test
	public void oneServiceRegistered() {
		final TestService service = mock(TestService.class);
		locator.registerService("test", service);
		assertEquals(locator.getServices().size(), 1);

		@SuppressWarnings("rawtypes")
		final Optional<IQueryService> registeredService = locator.getServiceFromQueryClass(TestQuery.class);
		assertTrue(registeredService.isPresent());
		assertSame(service, registeredService.get());
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void register_NullService() {
		thrown.expect(NullPointerException.class);

		locator.registerService("", null);
	}

}
