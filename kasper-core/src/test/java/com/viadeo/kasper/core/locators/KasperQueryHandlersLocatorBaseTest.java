// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.locators;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.locators.impl.DefaultQueryHandlersLocator;
import com.viadeo.kasper.core.resolvers.DomainResolver;
import com.viadeo.kasper.core.resolvers.QueryHandlerResolver;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.ddd.Domain;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@SuppressWarnings("serial")
public class KasperQueryHandlersLocatorBaseTest {

	private DefaultQueryHandlersLocator locator;

    @XKasperUnregistered
    private static final class TestDomain implements Domain { }

    @XKasperUnregistered
	private static final class TestQuery implements Query {}

    @XKasperUnregistered
    private static final class TestResult implements QueryResult {}

    @XKasperUnregistered
    @XKasperQueryHandler( domain = TestDomain.class )
	private static class TestHandler extends QueryHandler<TestQuery, TestResult> {
		@Override
		public QueryResponse<TestResult> retrieve(final QueryMessage<TestQuery> message) {
			throw new UnsupportedOperationException();
		}
	}

	// ------------------------------------------------------------------------

	@Before
	public void setUp() throws Exception {
        final DomainResolver domainResolver = new DomainResolver();

        final QueryHandlerResolver queryHandlerResolver = new QueryHandlerResolver();
        queryHandlerResolver.setDomainResolver(domainResolver);

		locator = new DefaultQueryHandlersLocator(queryHandlerResolver);
	}

	@Test
	public void getHandlerEmptyByDefault() {
		assertNotNull(locator.getHandlers());
		assertEquals(locator.getHandlers().size(), 0);
	}

    @Test
    public void getQueryHandlerByClass() {
        assertNotNull(locator.getQueryHandlerFromClass(TestHandler.class));
    }

	@Test
	@SuppressWarnings("rawtypes")
	public void getHandlerFromQueryClassEmptyState() {
		final Optional<QueryHandler> registeredHandler = locator.getHandlerFromQueryClass(TestQuery.class);
		assertFalse(registeredHandler.isPresent());
	}

	@Test
	public void oneHandlerRegistered() {
		final TestHandler handler = mock(TestHandler.class);
		locator.registerHandler("test", handler, TestDomain.class);
		assertEquals(locator.getHandlers().size(), 1);

		@SuppressWarnings("rawtypes")
		final Optional<QueryHandler> registeredHandler = locator.getHandlerFromQueryClass(TestQuery.class);
		assertTrue(registeredHandler.isPresent());
		assertSame(handler, registeredHandler.get());
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void registerNullHandler() {
		thrown.expect(NullPointerException.class);
		locator.registerHandler("", null, TestDomain.class);
	}

    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------

    @XKasperUnregistered
    private static class TestFilter implements QueryHandlerFilter { }

    @XKasperUnregistered
    private static class TestFilter2 implements QueryHandlerFilter { }

    @Test
    public void registerQueryHandlerFilter() {

        // Given
        final TestFilter filter = mock(TestFilter.class);
        final TestHandler handler = mock(TestHandler.class);
        final Class<? extends TestHandler> handlerClass = handler.getClass();

        // When
        locator.registerFilter("testFilter", filter);
        locator.registerHandler("testHandler", handler, TestDomain.class);
        locator.registerFilterForQueryHandler(handlerClass, filter.getClass());

        // Then
        assertEquals(1, locator.getFiltersForHandlerClass(handlerClass).size());

        // --

        // Given
        final TestFilter2 filter2 = mock(TestFilter2.class);

        // When
        locator.registerFilter("testFilter2", filter2);
        locator.registerFilterForQueryHandler(handlerClass, filter2.getClass());

        // Then
        assertEquals(2, locator.getFiltersForHandlerClass(handlerClass).size());

         // --

        // Given
        final QueryHandlerFilter filterGlobal = mock(QueryHandlerFilter.class);

        // When
        locator.registerFilter("testFilterGlobal", filterGlobal, true);

        // Then
        assertEquals(3, locator.getFiltersForHandlerClass(handlerClass).size());

    }

    // ------------------------------------------------------------------------

    @XKasperUnregistered
    private static final class TestDomain2 implements Domain { }

    @XKasperUnregistered
    private static final class TestQuery2 implements Query {}

    @XKasperUnregistered
    @XKasperQueryHandler( domain = TestDomain2.class )
    private static class TestHandler2 extends QueryHandler<TestQuery2, TestResult> {
        @Override
        public QueryResponse<TestResult> retrieve(final QueryMessage<TestQuery2> message) {
            throw new UnsupportedOperationException();
        }
    }

    @XKasperUnregistered
    private static class TestFilterDomain implements QueryHandlerFilter { }

    @Test
    public void registerStickyDomainFilter() {

        // Given
        final TestFilterDomain filter = new TestFilterDomain();
        final TestHandler handler = new TestHandler();
        final TestHandler2 handler2 = new TestHandler2();

        // When
        locator.registerFilter("testFilter", filter, true, TestDomain.class);
        locator.registerHandler("testHandler", handler, TestDomain.class);
        locator.registerHandler("testHandler2", handler2, TestDomain2.class);

        // Then
        assertEquals(1, locator.getFiltersForHandlerClass(handler.getClass()).size());
        assertEquals(0, locator.getFiltersForHandlerClass(handler2.getClass()).size());
    }

}
