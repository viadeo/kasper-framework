// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.platform.components.eventbus;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.AbstractContext;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.exception.KasperException;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.*;
import org.axonframework.eventhandling.async.*;
import org.axonframework.unitofwork.DefaultUnitOfWorkFactory;
import org.axonframework.unitofwork.NoTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 * Default Kasper event bus based on Axon's Cluster
 *
 * FIXME: Work in progress
 *
 */
public class KasperEventBus extends ClusteringEventBus {
    private static final Logger LOGGER = LoggerFactory.getLogger(KasperEventBus.class);

    private static final String KASPER_CLUSTER_NAME = "kasper";

    private static final int CORE_POOL_SIZE = 2;
    private static final int MAXIMUM_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 60L;
    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;

    private static final Policy DEFAULT_POLICY = Policy.ASYNCHRONOUS;
    public static enum Policy {
        SYNCHRONOUS, ASYNCHRONOUS
    }

    /*
     * TODO: make configurable
     * TODO: provides EventBusPolicy in order to configure the behaviour of the event bus
     */
    private static ClusterSelector getCluster(final Policy busPolicy) {
        if (Policy.ASYNCHRONOUS.equals(busPolicy)) {
            return new DefaultClusterSelector(
                new AsynchronousCluster(
                    KASPER_CLUSTER_NAME,
                    new ThreadPoolExecutor(
                            CORE_POOL_SIZE,
                            MAXIMUM_POOL_SIZE,
                            KEEP_ALIVE_TIME,
                            TIME_UNIT,
                            new SynchronousQueue<Runnable>(true)
                    ),
                    new DefaultUnitOfWorkFactory(new NoTransactionManager()),
                    new SequentialPolicy(),
                    new DefaultErrorHandler(RetryPolicy.proceed()) {
                        @Override
                        public RetryPolicy handleError(final Throwable exception,
                                                       final EventMessage<?> eventMessage,
                                                       final EventListener eventListener) {
                            /* TODO: store the error, generate error event */
                            LOGGER.error(String.format("Error %s occured during processing of event %s in listener %s ",
                                        exception.getMessage(),
                                        eventMessage.getPayload().getClass().getName(),
                                        eventListener.getClass().getName()
                            ));
                            return super.handleError(exception, eventMessage, eventListener);
                        }
                    }
                )
            );
        }
        if (Policy.SYNCHRONOUS.equals(busPolicy)) {
            return new DefaultClusterSelector();
        }

        throw new KasperException("Unmanaged Event bus policy " + busPolicy.toString());
    }

    // ------------------------------------------------------------------------

    public KasperEventBus(final Policy busPolicy) {
        super(getCluster(busPolicy));
    }

    public KasperEventBus() {
        super(getCluster(DEFAULT_POLICY));
    }

    // ------------------------------------------------------------------------

    public void publish(final Event event) {
        Preconditions.checkNotNull(event);
        Preconditions.checkState(event.getContext().isPresent(), "Context must be present !");

        final Context context = event.getContext().get();

        /* Sets a valid Kasper correlation id if required */
        if (AbstractContext.class.isAssignableFrom(context.getClass())) {
            final AbstractContext kasperContext = (AbstractContext) context;
            kasperContext.setValidKasperCorrelationId();
        }

        final Map<String, Object> metaData = Maps.newHashMap();
        metaData.put(Context.METANAME, Preconditions.checkNotNull(context));

        final GenericEventMessage<Event> eventMessageAxon =
                new GenericEventMessage<>(event, metaData);

        this.publish(eventMessageAxon);
    }

    public void publish(final Event event, final Context context) {
        Preconditions.checkNotNull(event).setContext(context);
        this.publish(event);
    }

}
