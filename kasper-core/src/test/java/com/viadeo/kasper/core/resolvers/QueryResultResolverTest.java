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
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.query.CollectionQueryResult;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.locators.QueryHandlersLocator;
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
