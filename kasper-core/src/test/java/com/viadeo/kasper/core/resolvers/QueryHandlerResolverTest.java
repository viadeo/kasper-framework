// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.exception.KasperException;
import org.junit.Test;

import static org.junit.Assert.*;

public class QueryHandlerResolverTest {

    @XKasperUnregistered
    private static final class TestDomain implements Domain { }

    @XKasperUnregistered
    @XKasperQueryHandler(domain = TestDomain.class)
    private static final class TestQueryHandler extends QueryHandler { }

    @XKasperUnregistered
    private static final class TestQueryHandler2 extends QueryHandler { }

    @XKasperUnregistered
    private static final class TestQuery implements Query { }

    @XKasperUnregistered
    private static final class TestQueryResult implements QueryResult { }

    @XKasperUnregistered
    private static final class TestQueryHandler3 extends QueryHandler<TestQuery, TestQueryResult> { }

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
            final Class<? extends Query> query = resolver.getQueryClass(TestQueryHandler.class);
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
            final Class<? extends QueryResult> queryResult = resolver.getQueryResultClass(TestQueryHandler.class);
            fail();
        } catch (final KasperException e) {
            // Then should raise exception
        }
    }

}
