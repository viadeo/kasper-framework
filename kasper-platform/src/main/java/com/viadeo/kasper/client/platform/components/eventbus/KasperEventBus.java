// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client.platform.components.eventbus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/*
 * Default Kasper event bus based on Axon's Cluster
 *
 * FIXME: work in progress
 * FIXME: Work on better configuration handling and different default policies
 *
 */
public class KasperEventBus extends ClusteringEventBus {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperEventBus.class);
    private static final String KASPER_CLUSTER_NAME = "kasper";

    /* FIXME: make it configurable */
    private static final int CORE_POOL_SIZE = 50;
    private static final int MAXIMUM_POOL_SIZE = 200;
    private static final long KEEP_ALIVE_TIME = 60L;
    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;

    public static enum Policy {
        SYNCHRONOUS, ASYNCHRONOUS, USER
    }
    private static final Policy DEFAULT_POLICY = Policy.SYNCHRONOUS;

    private final Policy currentPolicy;
    private final Optional<KasperProcessorDownLatch> optionalProcessorDownLatch;
    private final List<PublicationHandler> publicationHandlers = Lists.newLinkedList();

    // ------------------------------------------------------------------------

    public interface PublicationHandler {
        void handlePublication(EventMessage eventMessage);
    }

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
                LOGGER.error(String.format(
                    "Error %s occured during processing of event %s in listener %s ",
                    exception.getMessage(),
                    eventMessage.getPayload().getClass().getName(),
                    eventListener.getClass().getName()
                ));
                return super.handleError(exception, eventMessage, eventListener);
            }
        };
    }

    /*
     * Return a default cluster selector using the specified error handler
     * FIXME: eventually manage with a correct transaction manager
     */
    public static ClusterSelector getCluster(
            final Policy busPolicy,
            final ErrorHandler errorHandler,
            final KasperProcessorDownLatch processorDownLatch) {

        if (Policy.ASYNCHRONOUS.equals(busPolicy)) {
            final BlockingQueue<Runnable> busQueue = new LinkedBlockingQueue<Runnable>();

            return new DefaultClusterSelector(
                new AsynchronousCluster(
                    KASPER_CLUSTER_NAME,
                    new ThreadPoolExecutor(
                        CORE_POOL_SIZE,
                        MAXIMUM_POOL_SIZE,
                        KEEP_ALIVE_TIME,
                        TIME_UNIT,
                        busQueue
                    ),
                    new DefaultUnitOfWorkFactory(new NoTransactionManager()),
                    new SequentialPolicy(),
                    errorHandler
                ) {
                    @Override
                    protected EventProcessor newProcessingScheduler(EventProcessor.ShutdownCallback shutDownCallback) {
                        final EventProcessor eventProcessor = super.newProcessingScheduler(
                            new KasperShutdownCallback(processorDownLatch, shutDownCallback)
                        );
                        processorDownLatch.process(eventProcessor);
                        return eventProcessor;
                    }
                }
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
        this(DEFAULT_POLICY);
    }

    /*
     * Build a Kasper event bus using the specified policy
     */
    public KasperEventBus(final Policy busPolicy) {
        this(busPolicy, new KasperProcessorDownLatch());
    }

    /*
     * Build a Kasper event bus from the specified axon ClusterSelector
     */
    public KasperEventBus(final ClusterSelector axonClusterSelector) {
        super(checkNotNull(axonClusterSelector));
        this.currentPolicy = Policy.USER;
        this.optionalProcessorDownLatch = Optional.absent();
    }

    /*
     * Build a Kasper event bus from the specified axon Cluster with default cluster selector
     */
    public KasperEventBus(final Cluster cluster) {
        super(new DefaultClusterSelector(checkNotNull(cluster)));
        this.currentPolicy = Policy.USER;
        this.optionalProcessorDownLatch = Optional.absent();
    }

    /*
     * Build an asynchronous Kasper event bus with the specified error handler
     */
    public KasperEventBus(final ErrorHandler errorHandler) {
        this(Policy.ASYNCHRONOUS, errorHandler, new KasperProcessorDownLatch());
    }

    public KasperEventBus(final Policy busPolicy, final KasperProcessorDownLatch processorDownLatch) {
        this(busPolicy, getDefaultErrorHandler(), processorDownLatch);
    }

    public KasperEventBus(
            final Policy busPolicy,
            final ErrorHandler errorHandler,
            final KasperProcessorDownLatch processorDownLatch
    ) {
        super(getCluster(checkNotNull(busPolicy), checkNotNull(errorHandler), checkNotNull(processorDownLatch)));
        this.currentPolicy = busPolicy;
        this.optionalProcessorDownLatch = Optional.of(processorDownLatch);
    }

    // ------------------------------------------------------------------------

    public void onEventPublished(final PublicationHandler publicationHandler) {
        this.publicationHandlers.add(publicationHandler);
    }

    protected void noticePublicationHandlers(final EventMessage event) {
        for (final PublicationHandler publicationHandler : publicationHandlers) {
            publicationHandler.handlePublication(event);
        }
    }

    // ------------------------------------------------------------------------

    public Policy getCurrentPolicy() {
        return this.currentPolicy;
    }

    // ------------------------------------------------------------------------

    @Override
    public void publish(final EventMessage... messages) {
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

        /* Publish to Axon bus implementation */
        this.publishToSuper(newMessages);

        /* Notice handlers about event publication */
        for (final EventMessage message : newMessages) {
            this.noticePublicationHandlers(message);
        }
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

    // ------------------------------------------------------------------------

    public Optional<Runnable> getShutdownHook(){
        if(optionalProcessorDownLatch.isPresent()) {
            return Optional.<Runnable>of(new Runnable() {
                @Override
                public void run() {
                    LOGGER.info("Starting shutdown : Event Processing");
                    System.out.println("Starting shutdown : Event Processing");
                    optionalProcessorDownLatch.get().await();
                    LOGGER.info("Shutdown complete : Event Processing");
                    System.out.println("Shutdown complete : Event Processing");
                }
            });
        }
        return Optional.absent();
    }

}
