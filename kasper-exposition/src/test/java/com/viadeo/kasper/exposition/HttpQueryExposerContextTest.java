// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.client.platform.domain.DefaultDomainBundle;
import com.viadeo.kasper.client.platform.domain.DomainBundle;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.QueryMessage;
import com.viadeo.kasper.core.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.component.event.EventListener;
import com.viadeo.kasper.core.component.saga.Saga;
import com.viadeo.kasper.exposition.http.HttpQueryExposerPlugin;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertTrue;

public class HttpQueryExposerContextTest extends BaseHttpExposerTest {

    // ------------------------------------------------------------------------

    public HttpQueryExposerContextTest() {
        Locale.setDefault(Locale.US);
    }

    @Override
    protected HttpQueryExposerPlugin createExposerPlugin() {
        return new HttpQueryExposerPlugin();
    }

    @Override
    protected DomainBundle getDomainBundle(){
        return new DefaultDomainBundle(
                Lists.<CommandHandler>newArrayList()
                , Lists.<QueryHandler>newArrayList(new ContextCheckQueryHandler())
                , Lists.<Repository>newArrayList()
                , Lists.<EventListener>newArrayList()
                , Lists.<Saga>newArrayList()
                , Lists.<QueryInterceptorFactory>newArrayList()
                , Lists.<CommandInterceptorFactory>newArrayList()
                , Lists.<EventInterceptorFactory>newArrayList()
                , new AccountDomain()
                , "AccountDomain"
        );
    }

    // ------------------------------------------------------------------------

    @Test
    public void testQueryHandler() {
        // Given
        final ContextCheckQuery query = new ContextCheckQuery(getContextName());

        // When
        final QueryResponse<ContextCheckResult> actual = client().query(
                getFullContext(), query, ContextCheckResult.class);

        // Then
        assertTrue(actual.isOK());
    }

    // ------------------------------------------------------------------------

    public static class TestDomain implements Domain { }

    public static class ContextCheckResult implements QueryResult {
        private static final long serialVersionUID = 6219709753203593506L;
    }

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
            return QueryResponse.of(new ContextCheckResult());
        }
    }

}
