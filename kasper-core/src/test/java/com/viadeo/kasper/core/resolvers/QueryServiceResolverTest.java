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
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.exception.KasperException;
import org.junit.Test;

import static org.junit.Assert.*;

public class QueryServiceResolverTest {

    @XKasperUnregistered
    private static final class TestDomain implements Domain { }

    @XKasperUnregistered
    @XKasperQueryService(domain = TestDomain.class)
    private static final class TestQueryService implements QueryService {
        @Override
        public QueryResponse retrieve(QueryMessage message) throws Exception { return null; }
    }

    @XKasperUnregistered
    private static final class TestQueryService2 implements QueryService {
        @Override
        public QueryResponse retrieve(QueryMessage message) throws Exception { return null; }
    }

    @XKasperUnregistered
    private static final class TestQuery implements Query { }

    @XKasperUnregistered
    private static final class TestQueryResult implements QueryResult { }

    @XKasperUnregistered
    private static final class TestQueryService3 implements  QueryService<TestQuery, TestQueryResult> {
        @Override
        public QueryResponse<TestQueryResult> retrieve(QueryMessage<TestQuery> message) throws Exception {
            return null;
        }
    }

    // ------------------------------------------------------------------------

    @Test
    public void testGetDomainWithDecoratedQueryService() {
        // Given
        final QueryServiceResolver resolver = new QueryServiceResolver();

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomainClass(TestQueryService.class);

        // Then
        assertTrue(domain.isPresent());
        assertEquals(TestDomain.class, domain.get());
    }

    @Test
    public void testGetDomainWithNonDecoratedQueryService() {
        // Given
        final QueryServiceResolver resolver = new QueryServiceResolver();

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomainClass(TestQueryService2.class);

        // Then
        assertFalse(domain.isPresent());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testGetQueryFromValidService() {
        // Given
        final QueryServiceResolver resolver = new QueryServiceResolver();

        // When
        final Class<? extends Query> query = resolver.getQueryClass(TestQueryService3.class);

        // Then
        assertEquals(TestQuery.class, query);
    }


    @Test
    public void testGetQueryFromInvalidService() {
        // Given
        final QueryServiceResolver resolver = new QueryServiceResolver();

        // When
        try {
            final Class<? extends Query> query = resolver.getQueryClass(TestQueryService.class);
            fail();
        } catch (final KasperException e) {
            // Then should raise exception
        }
    }

    // ------------------------------------------------------------------------

    @Test
    public void testGetQueryResultFromValidService() {
        // Given
        final QueryServiceResolver resolver = new QueryServiceResolver();

        // When
        final Class<? extends QueryResult> queryResult = resolver.getQueryResultClass(TestQueryService3.class);

        // Then
        assertEquals(TestQueryResult.class, queryResult);
    }


    @Test
    public void testGetQueryResultFromInvalidService() {
        // Given
        final QueryServiceResolver resolver = new QueryServiceResolver();

        // When
        try {
            final Class<? extends QueryResult> queryResult = resolver.getQueryResultClass(TestQueryService.class);
            fail();
        } catch (final KasperException e) {
            // Then should raise exception
        }
    }

}
