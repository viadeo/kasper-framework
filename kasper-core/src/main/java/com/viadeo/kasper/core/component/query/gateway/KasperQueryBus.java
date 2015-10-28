// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query.gateway;

import com.codahale.metrics.InstrumentedExecutorService;
import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.api.exception.KasperQueryException;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.QueryMessage;
import com.viadeo.kasper.core.component.query.interceptor.QueryHandlerInterceptorFactory;
import com.viadeo.kasper.core.context.CurrentContext;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import com.viadeo.kasper.core.interceptor.InterceptorFactory;
import org.axonframework.commandhandling.CommandCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public class KasperQueryBus {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperQueryBus.class);
    private static final String QUERY_THREAD_NAME = "query-thread";

    // ------------------------------------------------------------------------

    private final Executor executor;
    private final ConcurrentMap<String, QueryHandler<Query, QueryResult>> subscriptions;
    private final InterceptorChainRegistry<Query, QueryResponse<QueryResult>> interceptorChainRegistry;

    // ------------------------------------------------------------------------

    public KasperQueryBus(
            final MetricRegistry metricRegistry,
            final InterceptorChainRegistry<Query, QueryResponse<QueryResult>> interceptorChainRegistry
    ) {
        this(new InstrumentedExecutorService(
                Executors.newCachedThreadPool(
                        new ThreadFactoryBuilder()
                                .setNameFormat(QUERY_THREAD_NAME + "-%d")
                                .build()
                ),
                checkNotNull(metricRegistry, "metric registry may not be null"),
                QUERY_THREAD_NAME
            ),
            interceptorChainRegistry
        );
    }

    public KasperQueryBus(
            final Executor executor,
            final InterceptorChainRegistry<Query, QueryResponse<QueryResult>> interceptorChainRegistry
    ) {
        this.executor = checkNotNull(executor, "executor may not be null");
        this.interceptorChainRegistry = checkNotNull(interceptorChainRegistry, "interceptor chain registry may not be null");
        this.subscriptions = new ConcurrentHashMap<>();
    }

    // ------------------------------------------------------------------------

    protected <R> void dispatch(final QueryMessage<?> message, final CommandCallback<R> callback) {
        executor.execute(new DispatchQuery<>(message, callback));
    }

    private <R> void doDispatch(final QueryMessage<?> message, final CommandCallback<R> callback) {
        try {
            final Query query = message.getQuery();
            final Class<? extends Query> queryClass = query.getClass();
            final Context context = message.getContext();
            final QueryHandler<Query, QueryResult> handler = findQueryHandlerFor(message);

            /* Sets current thread context */
            CurrentContext.set(context);

            // Search for associated handler --------------------------------------
            LOGGER.debug("Retrieve request processor chain for query " + queryClass.getSimpleName());
            final Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> optionalRequestChain =
                    getInterceptorChain(handler);

            if ( ! optionalRequestChain.isPresent()) {
                throw new KasperException("Unable to find the handler implementing query class " + queryClass);
            }

            try {
                LOGGER.info("Call actor chain for query " + queryClass.getSimpleName());
                @SuppressWarnings("unchecked")
                R ret = (R) optionalRequestChain.get().next(query, context);
                callback.onSuccess(ret);
            } catch (final Exception e) {
                callback.onFailure(e);
            }

        } catch (Throwable throwable) {
            callback.onFailure(throwable);
        }
    }

    public void register(InterceptorFactory<Query, QueryResponse<QueryResult>> interceptorFactory) {
        interceptorChainRegistry.register(interceptorFactory);
    }

    public void register(final QueryHandler<Query, QueryResult> handler) {
        subscriptions.put(handler.getInputClass().getName(), handler);

        // create immediately the interceptor chain instead of lazy mode
        getInterceptorChain(handler);
    }

    private QueryHandler<Query, QueryResult> findQueryHandlerFor(final QueryMessage<?> message) {
        final String queryName = message.getQuery().getClass().getName();
        final QueryHandler<Query, QueryResult> handler = subscriptions.get(queryName);
        if (handler == null) {
            throw new KasperQueryException(format("No handler was subscribed to command [%s]", queryName));
        }
        return handler;
    }

    protected Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> getInterceptorChain(
            final QueryHandler<Query, QueryResult> queryHandler
    ) {
        checkNotNull(queryHandler);

        final Optional<QueryHandler<Query, QueryResult>> queryHandlerOptional = Optional.fromNullable(
                subscriptions.get(queryHandler.getInputClass().getName())
        );

        if ( ! queryHandlerOptional.isPresent()) {
            return Optional.absent();
        }

        final Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> chainOptional =
                interceptorChainRegistry.get(queryHandler.getHandlerClass());

        if (chainOptional.isPresent()) {
            return chainOptional;
        }

        return interceptorChainRegistry.create(
                queryHandler.getHandlerClass(),
                new QueryHandlerInterceptorFactory(queryHandler)
        );
    }

    // ------------------------------------------------------------------------

    private final class DispatchQuery<R> implements Runnable {

        private final QueryMessage<?> query;
        private final CommandCallback<R> callback;

        public DispatchQuery(QueryMessage<?> query, CommandCallback<R> callback) {
            this.query = query;
            this.callback = callback;
        }

        @Override
        public void run() {
            KasperQueryBus.this.doDispatch(query, callback);
        }
    }
}
