// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryService;
import com.viadeo.kasper.ddd.Domain;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class QueryResolverTest {

    @XKasperUnregistered
    private static final class TestDomain implements Domain {}

    @XKasperUnregistered
    private static final class TestQuery implements Query { }

    // ------------------------------------------------------------------------

    @Test
    public void testGetDomain() {
        // Given
        final QueryResolver resolver = new QueryResolver();
        final DomainResolver domainResolver = mock(DomainResolver.class);
        final QueryServiceResolver queryServiceResolver = mock(QueryServiceResolver.class);
        final QueryServicesLocator queryServicesLocator = mock(QueryServicesLocator.class);
        final QueryService queryService = mock(QueryService.class);

        resolver.setQueryServiceResolver(queryServiceResolver);
        resolver.setQueryServicesLocator(queryServicesLocator);
        resolver.setDomainResolver(domainResolver);

        when( queryServicesLocator.getServiceFromQueryClass(TestQuery.class) )
                .thenReturn(Optional.<QueryService>of(queryService) );

        when( queryServiceResolver.getDomain(queryService.getClass()) )
                .thenReturn(Optional.<Class<? extends Domain>>of(TestDomain.class));

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomain(TestQuery.class);

        // Then
        assertTrue(domain.isPresent());
        assertEquals(TestDomain.class, domain.get());

        verify(queryServicesLocator, times(1)).getServiceFromQueryClass(TestQuery.class);
        verifyNoMoreInteractions(queryServicesLocator);

        verify(queryServiceResolver, times(1)).getDomain(queryService.getClass());
        verifyNoMoreInteractions(queryServiceResolver);
    }

}
