// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.locators;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.component.query.AutowiredQueryHandler;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.QueryHandlerAdapter;
import com.viadeo.kasper.core.component.query.QueryMessage;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
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
	private static class TestHandler extends AutowiredQueryHandler<TestQuery, TestResult> {
		@Override
		public QueryResponse<TestResult> handle(final QueryMessage<TestQuery> message) {
			throw new UnsupportedOperationException();
		}
	}

	// ------------------------------------------------------------------------

	@Before
	public void setUp() throws Exception {
		locator = new DefaultQueryHandlersLocator();
	}

    // ------------------------------------------------------------------------

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
		final Optional<QueryHandler<Query, QueryResult>> registeredHandler =
                locator.getHandlerFromQueryClass(TestQuery.class);

		assertFalse(registeredHandler.isPresent());
	}

	@Test
	public void oneHandlerRegistered() {
		final TestHandler handler = new TestHandler();
		locator.registerHandler("test", handler, TestDomain.class);
		assertEquals(locator.getHandlers().size(), 1);

		@SuppressWarnings("rawtypes")
		final Optional<QueryHandler<Query, QueryResult>> registeredHandler =
                locator.getHandlerFromQueryClass(TestQuery.class);

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
    private static class TestHandler2 extends AutowiredQueryHandler<TestQuery2, TestResult> {
        @Override
        public QueryResponse<TestResult> handle(final QueryMessage<TestQuery2> message) {
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
