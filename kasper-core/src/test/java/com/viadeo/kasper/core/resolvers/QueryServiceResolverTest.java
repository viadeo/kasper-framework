// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.cqrs.query.QueryMessage;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.QueryService;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.ddd.Domain;
import org.junit.Test;

import static org.junit.Assert.*;

public class QueryServiceResolverTest {

    @XKasperUnregistered
    private static class TestDomain implements Domain { }

    @XKasperUnregistered
    @XKasperQueryService(domain = TestDomain.class)
    private static class TestQueryService implements QueryService {
        @Override
        public QueryResult retrieve(QueryMessage message) throws Exception { return null; }
    }

    @XKasperUnregistered
    private static class TestQueryService2 implements QueryService {
        @Override
        public QueryResult retrieve(QueryMessage message) throws Exception { return null; }
    }

    // ------------------------------------------------------------------------

    @Test
    public void testGetDomainWithDecoratedQueryService() {
        // Given
        final QueryServiceResolver resolver = new QueryServiceResolver();

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomain(TestQueryService.class);

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
                resolver.getDomain(TestQueryService2.class);

        // Then
        assertFalse(domain.isPresent());
    }

}
