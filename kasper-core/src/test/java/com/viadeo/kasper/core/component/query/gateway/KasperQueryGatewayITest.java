package com.viadeo.kasper.core.component.query.gateway;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.core.component.query.AutowiredQueryHandler;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import com.viadeo.kasper.core.interceptor.InterceptorFactory;
import com.viadeo.kasper.core.locators.DefaultQueryHandlersLocator;
import com.viadeo.kasper.core.locators.QueryHandlersLocator;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class KasperQueryGatewayITest {

    private KasperQueryGateway kasperQueryGateway;
    private QueryHandlersLocator queryHandlersLocator;
    private InterceptorChainRegistry<Query, QueryResponse<QueryResult>> interceptorChainRegistry;

    @XKasperQueryHandler(domain = Domain.class)
    private static class QueryHandlerForTest extends AutowiredQueryHandler<Query, QueryResult> { }

    @Before
    public void setUp() throws Exception {
        queryHandlersLocator = spy(new DefaultQueryHandlersLocator());
        final MetricRegistry metricRegistry = new MetricRegistry();
        interceptorChainRegistry = spy(new InterceptorChainRegistry<Query, QueryResponse<QueryResult>>());
        kasperQueryGateway = new KasperQueryGateway(queryHandlersLocator, metricRegistry, interceptorChainRegistry);
    }

    @Test
    public void testRegister() throws Exception {
        // Given
        final QueryHandlerForTest queryHandler = new QueryHandlerForTest();

        //when
        kasperQueryGateway.register(queryHandler);

        //then
        verify(queryHandlersLocator).registerHandler("QueryHandlerForTest", queryHandler, Domain.class);
        verify(interceptorChainRegistry).create(eq(queryHandler.getHandlerClass()), any(InterceptorFactory.class));

    }
}