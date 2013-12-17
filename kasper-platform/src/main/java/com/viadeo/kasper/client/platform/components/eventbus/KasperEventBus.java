// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client.platform.components.eventbus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.context.CurrentContext;
import com.viadeo.kasper.event.IEvent;
import com.viadeo.kasper.exception.KasperException;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.*;
import org.axonframework.eventhandling.async.*;
import org.axonframework.unitofwork.DefaultUnitOfWorkFactory;
import org.axonframework.unitofwork.NoTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.getMetricRegistry;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

/*
 * Default Kasper event bus based on Axon's Cluster
 *
 * FIXME: work in progress
 * FIXME: Work on better configuration handling and different default policies
 *
 */
public class KasperEventBus extends ClusteringEventBus {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperEventBus.class);
    private static final String GLOBAL_METER_EVENTS_NAME = name(KasperEventBus.class, "events");
    private static final String KASPER_CLUSTER_NAME = "kasper";

    /* FIXME: make it configurable */
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE = 100;
    private static final long KEEP_ALIVE_TIME = 60L;
    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;

    public static enum Policy {
        SYNCHRONOUS, ASYNCHRONOUS, USER
    }
    private static final Policy DEFAULT_POLICY = Policy.SYNCHRONOUS;

    private final Policy currentPolicy;

    // ------------------------------------------------------------------------

    /*
     * Return a default error handler
     */
    public static ErrorHandler getDefaultErrorHandler() {
        return new DefaultErrorHandler(RetryPolicy.proceed()) {
                        @Override
                        public RetryPolicy handleError(final Throwable exception,
                                                       final EventMessage eventMessage,
                                                       final EventListener eventListener) {
                            /* TODO: store the error, generate error event */
                            LOGGER.error(String.format("Error %s occured during processing of event %s in listener %s ",
                                        exception.getMessage(),
                                        eventMessage.getPayload().getClass().getName(),
                                        eventListener.getClass().getName()
                            ));
                            return super.handleError(exception, eventMessage, eventListener);
                        }
                   };
    }

    /*
     * Return a default cluster selector
     */
    public static ClusterSelector getCluster(final Policy busPolicy) {
        return getCluster(busPolicy, getDefaultErrorHandler());
    }

    /*
     * Return a default cluster selector using the specified error handler
     * FIXME: eventually manage with a correct transaction manager
     */
    public static ClusterSelector getCluster(final Policy busPolicy, final ErrorHandler errorHandler) {

        if (Policy.ASYNCHRONOUS.equals(busPolicy)) {
            return new DefaultClusterSelector(
                new AsynchronousCluster(
                    KASPER_CLUSTER_NAME,
                    new ThreadPoolExecutor(
                            CORE_POOL_SIZE,
                            MAXIMUM_POOL_SIZE,
                            KEEP_ALIVE_TIME,
                            TIME_UNIT,
                            new LinkedBlockingQueue<Runnable>()
                    ),
                    new DefaultUnitOfWorkFactory(new NoTransactionManager()),
                    new SequentialPolicy(),
                    errorHandler
                )
            );
        }

        if (Policy.SYNCHRONOUS.equals(busPolicy)) {
            return new DefaultClusterSelector();
        }

        throw new KasperException("Unmanaged explicit event bus policy " + busPolicy.toString());
    }

    // ------------------------------------------------------------------------

    /*
     * Build a default synchronous event bus
     */
    public KasperEventBus() {
        super(getCluster(DEFAULT_POLICY));
        this.currentPolicy = DEFAULT_POLICY;
    }

    /*
     * Build a Kasper event bus using the specified policy
     */
    public KasperEventBus(final Policy busPolicy) {
        super(getCluster(checkNotNull(busPolicy)));
        this.currentPolicy = busPolicy;
    }

    /*
     * Build a Kasper event bus from the specified axon ClusterSelector
     */
    public KasperEventBus(final ClusterSelector axonClusterSelector) {
        super(checkNotNull(axonClusterSelector));
        this.currentPolicy = Policy.USER;
    }

    /*
     * Build a Kasper event bus from the specified axon Cluster with default cluster selector
     */
    public KasperEventBus(final Cluster cluster) {
        super(new DefaultClusterSelector(checkNotNull(cluster)));
        this.currentPolicy = Policy.USER;
    }

    /*
     * Build an asynchronous Kasper event bus with the specified error handler
     */
    public KasperEventBus(final ErrorHandler errorHandler) {
        super(getCluster(Policy.ASYNCHRONOUS, errorHandler));
        this.currentPolicy = Policy.ASYNCHRONOUS;
    }

    // ------------------------------------------------------------------------

    public Policy getCurrentPolicy() {
        return this.currentPolicy;
    }

    // ------------------------------------------------------------------------

    @Override
    public void publish(final EventMessage... messages) {
        getMetricRegistry().meter(GLOBAL_METER_EVENTS_NAME).mark();

        final EventMessage[] newMessages;

        /* Add the context to messages if required */
        if (CurrentContext.value().isPresent()) {
            newMessages = new EventMessage[messages.length];
            for (int i = 0 ; i < messages.length ; i++) {
                final EventMessage message = messages[i];
                if ( ! message.getMetaData().containsKey(Context.METANAME)) {
                    final Map<String, Object> metaData = Maps.newHashMap();
                    metaData.put(Context.METANAME, CurrentContext.value().get());
                    newMessages[i] = message.andMetaData(metaData);
                } else {
                    newMessages[i] = message;
                }
            }
        } else {
            newMessages = messages;
        }

        this.publishToSuper(newMessages);
    }

    @VisibleForTesting
    void publishToSuper(final EventMessage... messages) {
        super.publish(messages);
    }

    public void publish(final IEvent event) {
        this.publish(GenericEventMessage.asEventMessage(event));
    }

    public void publishEvent(final Context context, final IEvent event) {
        this.publish(
                new GenericEventMessage<>(
                        checkNotNull(event),
                        new HashMap<String, Object>() {{
                            this.put(Context.METANAME, context);
                        }}
                )
        );
    }

}
