package com.viadeo.kasper.cqrs.query.impl;

import com.viadeo.kasper.core.locators.impl.DefaultQueryHandlersLocator;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryHandlerFilter;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.ddd.Domain;
import org.junit.After;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;

public class KasperQueryGatewayUTest {

    private final KasperQueryGateway queryGateway;
    private final DefaultQueryHandlersLocator queryHandlersLocator;

    public KasperQueryGatewayUTest(){
        queryHandlersLocator = mock(DefaultQueryHandlersLocator.class);
        queryGateway = new KasperQueryGateway(queryHandlersLocator);
    }

    @After
    public void clean(){
        reset(queryHandlersLocator);
    }

    @Test(expected = NullPointerException.class)
    public void register_withNullAsQueryHandler_shouldThrownException(){
        // Given
        QueryHandler queryHandler = null;

        // When
        queryGateway.register(queryHandler);

        // Then throws an exception
    }

    @Test
    public void register_withQueryHandler_shouldBeRegistered(){
        // Given
        QueryHandler queryHandler = new QueryHandlerForTest();

        // When
        queryGateway.register(queryHandler);

        // Then
        verify(queryHandlersLocator).registerHandler(refEq("QueryHandlerForTest"), refEq(queryHandler), refEq(Domain.class));
        verifyNoMoreInteractions(queryHandlersLocator);

        assertEquals(queryGateway, queryHandler.getQueryGateway());
    }

    @Test
    public void register_withQueryHandler_withFilters_shouldBeRegistered(){
        // Given
        QueryHandler queryHandler = new QueryHandlerWithFiltersForTest();

        // When
        queryGateway.register(queryHandler);

        // Then
        verify(queryHandlersLocator).registerFilterForQueryHandler(refEq(QueryHandlerWithFiltersForTest.class), refEq(Filter1.class));
        verify(queryHandlersLocator).registerHandler(refEq("QueryHandlerWithFiltersForTest"), refEq(queryHandler), refEq(Domain.class));
        verifyNoMoreInteractions(queryHandlersLocator);

        assertEquals(queryGateway, queryHandler.getQueryGateway());
    }

    @XKasperQueryHandler(domain = Domain.class)
    private static class QueryHandlerForTest extends QueryHandler<Query, QueryResult> { }

    @XKasperQueryHandler(domain = Domain.class, filters = {Filter1.class/*, Filter2.class*/})
    private static class QueryHandlerWithFiltersForTest extends QueryHandler<Query, QueryResult> { }

    private final class Filter1 implements QueryHandlerFilter { }
}
