// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query.gateway;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.QueryHandlerAdapter;
import com.viadeo.kasper.core.component.query.QueryMessage;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import com.viadeo.kasper.core.interceptor.InterceptorFactory;
import com.viadeo.kasper.core.locators.DefaultQueryHandlersLocator;
import com.viadeo.kasper.core.locators.QueryHandlersLocator;
import org.axonframework.commandhandling.callbacks.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The Kasper gateway base implementation
 */
public class KasperQueryGateway implements QueryGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperQueryGateway.class);

    private final KasperQueryBus queryBus;
    private final QueryHandlersLocator queryHandlersLocator;

    // -----------------------------------------------------------------------

    public KasperQueryGateway(final MetricRegistry metricRegistry) {
        this(
                new KasperQueryBus(metricRegistry, new InterceptorChainRegistry<Query, QueryResponse<QueryResult>>()),
                new DefaultQueryHandlersLocator()
        );
    }

    public KasperQueryGateway(
            final KasperQueryBus queryBus,
            final QueryHandlersLocator queryHandlersLocator
    ) {
        this.queryHandlersLocator = checkNotNull(queryHandlersLocator);
        this.queryBus = checkNotNull(queryBus);
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public <RESULT extends QueryResult> QueryResponse<RESULT> retrieve(final Query query, final Context context)
            throws Exception
    {
        checkNotNull(context);
        checkNotNull(query);

        FutureCallback<QueryResponse<RESULT>> future = new FutureCallback<>();

        queryBus.dispatch(new QueryMessage<>(context, query), future);

        try {
            return future.get();
        } catch (ExecutionException e) {
            if (e.getCause() != null && e.getCause() instanceof Exception) {
                throw (Exception) e.getCause();
            } else {
                throw e;
            }
        }
    }

    @Override
    public <RESULT extends QueryResult> Future<QueryResponse<RESULT>> retrieveForFuture(Query query, Context context)
            throws Exception
    {
        checkNotNull(context);
        checkNotNull(query);

        FutureCallback<QueryResponse<RESULT>> future = new FutureCallback<>();

        queryBus.dispatch(new QueryMessage<>(context, query), future);

        return future;
    }

    // ------------------------------------------------------------------------

    /**
     * Register a query handler adapter to the gateway
     *
     * @param name the name of the adapter
     * @param adapter the query handler adapter to register
     * @param global the kind of the adapter. If true then the adapter will be applied to every query handler component.
     *               Otherwise the  adapter will be applied only on the component whose reference it
     */
    @Deprecated
    public void register(final String name, final QueryHandlerAdapter adapter, final boolean global) {
        queryHandlersLocator.registerAdapter(
            checkNotNull(name),
            checkNotNull(adapter),
            checkNotNull(global)
        );
    }

    /**
     * Register an interceptor factory to the gateway
     *
     * @param interceptorFactory the query interceptor factory to register
     */
    public void register(final InterceptorFactory<Query, QueryResponse<QueryResult>> interceptorFactory) {
        checkNotNull(interceptorFactory);
        LOGGER.info("Registering the query interceptor factory : " + interceptorFactory.getClass().getSimpleName());

        queryBus.register(interceptorFactory);
    }

    /**
     * Register a query handler to the gateway
     *
     * @param queryHandler the query handler to register
     */
    @SuppressWarnings("unchecked")
    public void register(final QueryHandler queryHandler) {
        checkNotNull(queryHandler);

        final Class<? extends QueryHandler> queryHandlerClass = queryHandler.getHandlerClass();
        LOGGER.info("Registering the query handler : " + queryHandlerClass.getName());

        final XKasperQueryHandler annotation = queryHandlerClass.getAnnotation(XKasperQueryHandler.class);

        final String handlerName;
        if (annotation.name().isEmpty()) {
            handlerName = queryHandlerClass.getSimpleName();
        } else {
            handlerName = annotation.name();
        }

        queryHandlersLocator.registerHandler(handlerName, queryHandler, annotation.domain());

        queryBus.register(queryHandler);

    }

}
