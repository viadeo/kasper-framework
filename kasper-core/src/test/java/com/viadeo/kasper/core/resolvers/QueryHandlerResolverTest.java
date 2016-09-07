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
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.component.query.AutowiredQueryHandler;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import org.junit.Test;

import static org.junit.Assert.*;

public class QueryHandlerResolverTest {

    @XKasperUnregistered
    private static final class TestDomain implements Domain { }

    @XKasperUnregistered
    @XKasperQueryHandler(domain = TestDomain.class)
    private static final class TestQueryHandler extends AutowiredQueryHandler { }

    @XKasperUnregistered
    private static final class TestQueryHandler2 extends AutowiredQueryHandler { }

    @XKasperUnregistered
    private static final class TestQuery implements Query { }

    @XKasperUnregistered
    private static final class TestQueryResult implements QueryResult { }

    @XKasperUnregistered
    private static final class TestQueryHandler3 extends AutowiredQueryHandler<TestQuery, TestQueryResult> { }

    // ------------------------------------------------------------------------

    @Test
    public void testGetDomainWithDecoratedQueryHandler() {
        // Given
        final QueryHandlerResolver resolver = new QueryHandlerResolver();

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomainClass(TestQueryHandler.class);

        // Then
        assertTrue(domain.isPresent());
        assertEquals(TestDomain.class, domain.get());
    }

    @Test
    public void testGetDomainWithNonDecoratedQueryHandler() {
        // Given
        final QueryHandlerResolver resolver = new QueryHandlerResolver();

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomainClass(TestQueryHandler2.class);

        // Then
        assertFalse(domain.isPresent());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testGetQueryFromValidService() {
        // Given
        final QueryHandlerResolver resolver = new QueryHandlerResolver();

        // When
        final Class<? extends Query> query = resolver.getQueryClass(TestQueryHandler3.class);

        // Then
        assertEquals(TestQuery.class, query);
    }


    @Test
    public void testGetQueryFromInvalidHandler() {
        // Given
        final QueryHandlerResolver resolver = new QueryHandlerResolver();

        // When
        try {
            resolver.getQueryClass(TestQueryHandler.class);
            fail();
        } catch (final KasperException e) {
            // Then should raise exception
        }
    }

    // ------------------------------------------------------------------------

    @Test
    public void testGetQueryResultFromValidHandler() {
        // Given
        final QueryHandlerResolver resolver = new QueryHandlerResolver();

        // When
        final Class<? extends QueryResult> queryResult = resolver.getQueryResultClass(TestQueryHandler3.class);

        // Then
        assertEquals(TestQueryResult.class, queryResult);
    }


    @Test
    public void testGetQueryResultFromInvalidHandler() {
        // Given
        final QueryHandlerResolver resolver = new QueryHandlerResolver();

        // When
        try {
            resolver.getQueryResultClass(TestQueryHandler.class);
            fail();
        } catch (final KasperException e) {
            // Then should raise exception
        }
    }

}
