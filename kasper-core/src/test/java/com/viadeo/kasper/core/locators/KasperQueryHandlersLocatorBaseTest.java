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
		final Optional<QueryHandler<Query, QueryResult>> registeredHandler = locator.getHandlerFromQueryClass(TestQuery.class);
		assertFalse(registeredHandler.isPresent());
	}

	@Test
	public void oneHandlerRegistered() {
		final TestHandler handler = mock(TestHandler.class);
		locator.registerHandler("test", handler, TestDomain.class);
		assertEquals(locator.getHandlers().size(), 1);

		@SuppressWarnings("rawtypes")
		final Optional<QueryHandler<Query, QueryResult>> registeredHandler = locator.getHandlerFromQueryClass(TestQuery.class);
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
    private static class TestAdapter implements QueryHandlerAdapter { }

    @XKasperUnregistered
    private static class TestAdapter2 implements QueryHandlerAdapter { }

    @Test
    public void registerQueryHandlerFilter() {

        // Given
        final TestAdapter adapter = mock(TestAdapter.class);
        final TestHandler handler = mock(TestHandler.class);
        final Class<? extends TestHandler> handlerClass = handler.getClass();

        // When
        locator.registerAdapter("testAdapter", adapter);
        locator.registerHandler("testHandler", handler, TestDomain.class);
        locator.registerAdapterForQueryHandler(handlerClass, adapter.getClass());

        // Then
        assertEquals(1, locator.getAdaptersForHandlerClass(handlerClass).size());

        // --

        // Given
        final TestAdapter2 adapter2 = mock(TestAdapter2.class);

        // When
        locator.registerAdapter("testAdapter2", adapter2);
        locator.registerAdapterForQueryHandler(handlerClass, adapter2.getClass());

        // Then
        assertEquals(2, locator.getAdaptersForHandlerClass(handlerClass).size());

         // --

        // Given
        final QueryHandlerAdapter adapterGlobal = mock(QueryHandlerAdapter.class);

        // When
        locator.registerAdapter("testAdapterGlobal", adapterGlobal, true);

        // Then
        assertEquals(3, locator.getAdaptersForHandlerClass(handlerClass).size());

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
    private static class TestAdapterDomain implements QueryHandlerAdapter { }

    @Test
    public void registerStickyDomainFilter() {

        // Given
        final TestAdapterDomain adapter = new TestAdapterDomain();
        final TestHandler handler = new TestHandler();
        final TestHandler2 handler2 = new TestHandler2();

        // When
        locator.registerAdapter("testAdapter", adapter, true, TestDomain.class);
        locator.registerHandler("testHandler", handler, TestDomain.class);
        locator.registerHandler("testHandler2", handler2, TestDomain2.class);

        // Then
        assertEquals(1, locator.getAdaptersForHandlerClass(handler.getClass()).size());
        assertEquals(0, locator.getAdaptersForHandlerClass(handler2.getClass()).size());
    }

}
