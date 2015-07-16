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
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.component.Domain;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class QueryResolverTest {

    @XKasperUnregistered
    private static final class TestDomain implements Domain {}

    @XKasperUnregistered
    private static final class TestQuery implements Query {
        private static final long serialVersionUID = 9074545422570302563L;
    }

    // ------------------------------------------------------------------------

    @Test
    public void testGetDomain() {
        // Given
        final QueryResolver resolver = new QueryResolver();
        final DomainResolver domainResolver = mock(DomainResolver.class);
        final QueryHandlerResolver queryHandlerResolver = mock(QueryHandlerResolver.class);
        final QueryHandlersLocator queryHandlersLocator = mock(QueryHandlersLocator.class);
        final QueryHandler queryHandler = mock(QueryHandler.class);

        resolver.setQueryHandlerResolver(queryHandlerResolver);
        resolver.setQueryHandlersLocator(queryHandlersLocator);
        resolver.setDomainResolver(domainResolver);

        when( queryHandlersLocator.getHandlerFromQueryClass(TestQuery.class) )
                .thenReturn(Optional.<QueryHandler<Query, QueryResult>>of(queryHandler) );

        when( queryHandlerResolver.getDomainClass(queryHandler.getClass()) )
                .thenReturn(Optional.<Class<? extends Domain>>of(TestDomain.class));

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomainClass(TestQuery.class);

        // Then
        assertTrue(domain.isPresent());
        assertEquals(TestDomain.class, domain.get());

        verify(queryHandlersLocator, times(1)).getHandlerFromQueryClass(TestQuery.class);
        verifyNoMoreInteractions(queryHandlersLocator);

        verify(queryHandlerResolver, times(1)).getDomainClass(queryHandler.getClass());
        verifyNoMoreInteractions(queryHandlerResolver);
    }

}
