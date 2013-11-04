// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.core.locators.QueryHandlersLocator;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.ddd.Domain;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import java.util.Locale;

import static com.viadeo.kasper.exposition.TestContexts.CONTEXT_FULL;
import static com.viadeo.kasper.exposition.TestContexts.context_full;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HttpQueryExposerContextTest extends BaseHttpExposerTest<HttpQueryExposer> {

    // ------------------------------------------------------------------------

    public HttpQueryExposerContextTest() {
        Locale.setDefault(Locale.US);
    }

    @Override
    protected HttpQueryExposer createExposer(final ApplicationContext ctx) {
        return new HttpQueryExposer(ctx.getBean(QueryGateway.class), ctx.getBean(QueryHandlersLocator.class));
    }

    // ------------------------------------------------------------------------

    @Test
    public void testQueryHandlerThrowingException() {
        // Given
        final ContextCheckQuery query = new ContextCheckQuery(CONTEXT_FULL);

        // When
        final QueryResponse<ContextCheckResult> actual = client().query(
                context_full, query, ContextCheckResult.class);

        // Then
        assertFalse(actual.isOK());
    }

    // ------------------------------------------------------------------------

    public static class TestDomain implements Domain { }

    public static class ContextCheckResult implements QueryResult { }

    public static class ContextCheckQuery implements Query {
        private static final long serialVersionUID = 674422094842929150L;

        private String contextName;

        ContextCheckQuery() { }

        ContextCheckQuery(final String contextName) {
            this.contextName = contextName;
        }

        public String getContextName() {
            return this.contextName;
        }

    }

    @XKasperQueryHandler(domain = AccountDomain.class)
    public static class ContextCheckQueryHandler implements QueryHandler<ContextCheckQuery, ContextCheckResult> {
        @Override
        public QueryResponse<ContextCheckResult> retrieve(final QueryMessage<ContextCheckQuery> message) throws Exception {
            if (message.getQuery().getContextName().contentEquals(CONTEXT_FULL)) {
                assertTrue(((DefaultContext) message.getContext()).equals(context_full));
            }
            return QueryResponse.of(new ContextCheckResult());
        }
    }

}
