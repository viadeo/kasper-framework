// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.viadeo.kasper.context.Context;
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
    public void testQueryHandler() {
        // Given
        final ContextCheckQuery query = new ContextCheckQuery(CONTEXT_FULL);

        // When
        final QueryResponse<ContextCheckResult> actual = client().query(
                context_full, query, ContextCheckResult.class);

        // Then
        assertTrue(actual.isOK());
    }

    // ------------------------------------------------------------------------

    public static class TestDomain implements Domain { }

    public static class ContextCheckResult implements QueryResult { }

    public static class ContextCheckQuery implements Query {
        private static final long serialVersionUID = 674422094842929150L;

        private String contextName;

        @JsonCreator
        public ContextCheckQuery(@JsonProperty("contextName") final String contextName) {
            this.contextName = contextName;
        }

        public String getContextName() {
            return this.contextName;
        }

    }

    @XKasperQueryHandler(domain = AccountDomain.class)
    public static class ContextCheckQueryHandler extends QueryHandler<ContextCheckQuery, ContextCheckResult> {
        @Override
        public QueryResponse<ContextCheckResult> retrieve(final QueryMessage<ContextCheckQuery> message) throws Exception {
            if (message.getQuery().getContextName().contentEquals(CONTEXT_FULL)) {
                /* Kasper correlation id is set by the gateway or auto-expo layer */
                final Context copy_context_full = context_full.child();
                ((DefaultContext) copy_context_full).setKasperCorrelationId(((DefaultContext) message.getContext()).getKasperCorrelationId());

                assertTrue(((DefaultContext) message.getContext()).equals(copy_context_full));
            }
            return QueryResponse.of(new ContextCheckResult());
        }
    }

}
