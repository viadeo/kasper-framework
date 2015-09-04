// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query.gateway;

import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.component.query.AutowiredQueryHandler;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryCache;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryFilter;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import com.viadeo.kasper.core.locators.DefaultQueryHandlersLocator;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;

public class KasperQueryGatewayUTest {

    private final KasperQueryGateway queryGateway;
    private final DefaultQueryHandlersLocator queryHandlersLocator;
    private final InterceptorChainRegistry interceptorChainRegistry;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // ------------------------------------------------------------------------

    @XKasperUnregistered
    @XKasperQueryHandler(domain = Domain.class)
    public static class QueryHandlerForTest extends AutowiredQueryHandler<Query, QueryResult> { }

    @XKasperUnregistered
    @XKasperQueryHandler(domain = Domain.class)
    @XKasperQueryFilter(value = {InterceptorA.class})
    public static class QueryHandlerWithFiltersForTest extends AutowiredQueryHandler<Query, QueryResult> { }

    @XKasperUnregistered
    @XKasperQueryHandler(domain = Domain.class, cache = @XKasperQueryCache(enabled = true))
    public static class QueryHandlerWithCacheForTest extends AutowiredQueryHandler<Query, QueryResult> { }

    public static class InterceptorA implements QueryInterceptor<Query, QueryResult> {
        @Override
        public QueryResponse<QueryResult> process(Query query, Context context, InterceptorChain<Query, QueryResponse<QueryResult>> chain) throws Exception {
            return chain.next(query, context);
        }
    }

    // ------------------------------------------------------------------------

    public KasperQueryGatewayUTest() {
        queryHandlersLocator = mock(DefaultQueryHandlersLocator.class);
        interceptorChainRegistry = mock(InterceptorChainRegistry.class);
        queryGateway = new KasperQueryGateway(
                new KasperQueryBus(interceptorChainRegistry),
                queryHandlersLocator
        );
    }

    @After
    public void clean() {
        reset(queryHandlersLocator, interceptorChainRegistry);
    }

    // ------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void register_withNullAsQueryHandler_shouldThrownException() {
        // Given
        final AutowiredQueryHandler queryHandler = null;

        // When
        queryGateway.register(queryHandler);

        // Then throws an exception
    }

    @Test
    public void register_withQueryHandler_shouldBeRegistered() {
        // Given
        final AutowiredQueryHandler queryHandler = new QueryHandlerForTest();

        // When
        queryGateway.register(queryHandler);

        // Then
        verify(queryHandlersLocator).registerHandler(refEq("QueryHandlerForTest"), refEq(queryHandler), refEq(Domain.class));
        verifyNoMoreInteractions(queryHandlersLocator);
    }

}
