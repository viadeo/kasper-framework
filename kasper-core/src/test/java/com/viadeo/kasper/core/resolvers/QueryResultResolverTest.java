// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.locators.QueryHandlersLocator;
import com.viadeo.kasper.cqrs.query.CollectionQueryResult;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.exception.KasperException;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class QueryResultResolverTest {

    @XKasperUnregistered
    private static final class TestDomain implements Domain {}

    @XKasperUnregistered
    private static final class TestQueryResult implements QueryResult { }

    @XKasperUnregistered
    private static final class UnknownCollectionQueryResult extends CollectionQueryResult { }

    @XKasperUnregistered
    private static final class TestCollectionQueryResult extends CollectionQueryResult<TestQueryResult> { }

    // ------------------------------------------------------------------------

    private QueryResultResolver resolver;
    private QueryHandlerResolver queryHandlerResolver;
    private QueryHandlersLocator queryHandlersLocator;

    @Before
    public void setUp() {
        final DomainResolver domainResolver = mock(DomainResolver.class);
        queryHandlerResolver = mock(QueryHandlerResolver.class);
        queryHandlersLocator = mock(QueryHandlersLocator.class);

        resolver = new QueryResultResolver();
        resolver.setQueryHandlerResolver(queryHandlerResolver);
        resolver.setQueryHandlersLocator(queryHandlersLocator);
        resolver.setDomainResolver(domainResolver);
    }

    @Test
    public void testGetDomain() {
        // Given
        final QueryHandler queryHandler = mock(QueryHandler.class);

        when( queryHandlersLocator.getHandlersFromQueryResultClass(TestQueryResult.class) )
                .thenReturn(Collections.singletonList(queryHandler) );

        when( queryHandlerResolver.getDomainClass(queryHandler.getClass()) )
                .thenReturn(Optional.<Class<? extends Domain>>of(TestDomain.class));

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomainClass(TestQueryResult.class);

        // Then
        assertTrue(domain.isPresent());
        assertEquals(TestDomain.class, domain.get());

        verify(queryHandlersLocator, times(1)).getHandlersFromQueryResultClass(TestQueryResult.class);
        verifyNoMoreInteractions(queryHandlersLocator);

        verify(queryHandlerResolver, times(1)).getDomainClass(queryHandler.getClass());
        verifyNoMoreInteractions(queryHandlerResolver);
    }

    @Test(expected = KasperException.class)
    public void getElementClass_fromQueryResult_throwException() {
        resolver.getElementClass(UnknownCollectionQueryResult.class);
    }

    @Test
    public void getElementClass_fromCollectionQueryResult_returnElementClass() {
        // Given

        // When
        final Class<? extends QueryResult> elementClass = resolver.getElementClass(TestCollectionQueryResult.class);

        // Then
        assertNotNull(elementClass);
        assertEquals(TestQueryResult.class, elementClass);
    }

}
