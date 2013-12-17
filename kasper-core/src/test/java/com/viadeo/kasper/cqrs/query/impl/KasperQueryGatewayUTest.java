// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.impl;

import com.viadeo.kasper.core.locators.impl.DefaultQueryHandlersLocator;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryHandlerAdapter;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.exception.KasperException;
import org.junit.After;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;

public class KasperQueryGatewayUTest {

    private final KasperQueryGateway queryGateway;
    private final DefaultQueryHandlersLocator queryHandlersLocator;

    @XKasperQueryHandler(domain = Domain.class)
    private static class QueryHandlerForTest extends QueryHandler<Query, QueryResult> { }

    @XKasperQueryHandler(domain = Domain.class, adapters = { Adapter1.class })
    private static class QueryHandlerWithAdaptersForTest extends QueryHandler<Query, QueryResult> { }

    private final class Adapter1 implements QueryHandlerAdapter { }

    // ------------------------------------------------------------------------

    public KasperQueryGatewayUTest(){
        queryHandlersLocator = mock(DefaultQueryHandlersLocator.class);
        queryGateway = new KasperQueryGateway(queryHandlersLocator);
    }

    @After
    public void clean(){
        reset(queryHandlersLocator);
    }

    // ------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void register_withNullAsQueryHandler_shouldThrownException(){
        // Given
        final QueryHandler queryHandler = null;

        // When
        queryGateway.register(queryHandler);

        // Then throws an exception
    }

    @Test
    public void register_withQueryHandler_shouldBeRegistered(){
        // Given
        final QueryHandler queryHandler = new QueryHandlerForTest();

        // When
        queryGateway.register(queryHandler);

        // Then
        verify(queryHandlersLocator).registerHandler(refEq("QueryHandlerForTest"), refEq(queryHandler), refEq(Domain.class));
        verifyNoMoreInteractions(queryHandlersLocator);

        assertEquals(queryGateway, queryHandler.getQueryGateway());
    }

    @Test(expected = KasperException.class)
    public void register_withQueryHandler_withoutRegisteredAdapters_shouldThrownException(){
        // Given
        final QueryHandler queryHandler = new QueryHandlerWithAdaptersForTest();

        // When
        queryGateway.register(queryHandler);

        // Then throw an exception
    }

    @Test(expected = KasperException.class)
    public void register_withQueryHandler_withRegisteredAdapters_shouldThrownException(){
        // Given
        final QueryHandler queryHandler = new QueryHandlerWithAdaptersForTest();
        queryGateway.register("test", new Adapter1());
        reset(queryHandlersLocator);

        // When
        queryGateway.register(queryHandler);

        // Then
        verify(queryHandlersLocator).containsAdapter(Adapter1.class);
        verify(queryHandlersLocator).registerAdapterForQueryHandler(refEq(QueryHandlerWithAdaptersForTest.class), refEq(Adapter1.class));
        verify(queryHandlersLocator).registerHandler(refEq("QueryHandlerWithAdaptersForTest"), refEq(queryHandler), refEq(Domain.class));
        verifyNoMoreInteractions(queryHandlersLocator);

        assertEquals(queryGateway, queryHandler.getQueryGateway());
    }

}
