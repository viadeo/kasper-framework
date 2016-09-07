// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.eventbus;

import com.codahale.metrics.InstrumentedExecutorService;
import com.codahale.metrics.MetricRegistry;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.core.component.event.interceptor.EventHandlerInterceptorFactory;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import com.viadeo.kasper.core.interceptor.InterceptorFactory;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.*;
import org.axonframework.eventhandling.async.*;
import org.axonframework.unitofwork.DefaultUnitOfWorkFactory;
import org.axonframework.unitofwork.NoTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static com.google.common.base.Preconditions.checkNotNull;

/*
 * Default Kasper event bus based on Axon's Cluster
 *
 * FIXME: work in progress
 * FIXME: Work on better configuration handling and different default policies
 *
 */
public class KasperEventBus extends ClusteringEventBus {

    public enum Policy {
        SYNCHRONOUS, ASYNCHRONOUS, USER
    }

    public interface PublicationHandler {
        void handlePublication(EventMessage eventMessage);
        void shutdown();
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
                    "Error %s occurred during processing of event %s in listener %s ",
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
     * FIXME: eventually manage with a complete transaction manager
     */
    public static ClusterSelector getCluster(
            final Policy busPolicy,
            final ErrorHandler errorHandler,
            final MetricRegistry metricRegistry,
            final KasperProcessorDownLatch processorDownLatch) {

        if (Policy.ASYNCHRONOUS.equals(busPolicy)) {
            final BlockingQueue<Runnable> busQueue = new LinkedBlockingQueue<>();

            final ExecutorService threadPool = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TIME_UNIT,
                busQueue
            );

            final ExecutorService instrumentedThreadPool = new InstrumentedExecutorService(
                    threadPool, metricRegistry, KasperEventBus.class.getName()
            );

            return new DefaultClusterSelector(
                new AsynchronousCluster(
                    KASPER_CLUSTER_NAME,
                    instrumentedThreadPool,
                    new DefaultUnitOfWorkFactory(new NoTransactionManager()),
                    new SequentialPolicy(),
                    errorHandler
                ) {
                    @Override
                    protected EventProcessor newProcessingScheduler(
                            final EventProcessor.ShutdownCallback shutDownCallback,
                            final Set<EventListener> eventListeners,
                            final MultiplexingEventProcessingMonitor eventProcessingMonitor) {
                        final EventProcessor eventProcessor = super.newProcessingScheduler(
                            new KasperShutdownCallback(processorDownLatch, shutDownCallback),
                            eventListeners,
                            eventProcessingMonitor
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

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperEventBus.class);
    private static final String KASPER_CLUSTER_NAME = "kasper";
    private static final Policy DEFAULT_POLICY = Policy.SYNCHRONOUS;

    /* FIXME: make it configurable */
    private static final int CORE_POOL_SIZE = 50;
    private static final int MAXIMUM_POOL_SIZE = 200;
    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;
    private static final long KEEP_ALIVE_TIME = 60L;

    // ------------------------------------------------------------------------

    private final InterceptorChainRegistry<Event, Void> interceptorChainRegistry;
    private final Policy currentPolicy;
    private final Optional<KasperProcessorDownLatch> optionalProcessorDownLatch;
    private final List<PublicationHandler> publicationHandlers = Lists.newLinkedList();
    private final MetricRegistry metricRegistry;
    private final String publishMetricName;

    /**
     * Build a default synchronous event bus
     *
     * @param metricRegistry the metric registry
     */
    public KasperEventBus(final MetricRegistry metricRegistry) {
        this(metricRegistry, DEFAULT_POLICY);
    }

    /**
     * Build a Kasper event bus using the specified policy
     *
     * @param busPolicy the bus policy
     * @param metricRegistry the metric registry
     */
    public KasperEventBus(final MetricRegistry metricRegistry, final Policy busPolicy) {
        this(metricRegistry, busPolicy, new KasperProcessorDownLatch(metricRegistry));
    }

    /**
     * Build a Kasper event bus from the specified axon ClusterSelector
     *
     * @param axonClusterSelector the cluster selector
     * @param metricRegistry the metric registry
     */
    public KasperEventBus(final MetricRegistry metricRegistry, final ClusterSelector axonClusterSelector) {
        super(checkNotNull(axonClusterSelector));
        this.currentPolicy = Policy.USER;
        this.optionalProcessorDownLatch = Optional.absent();
        this.metricRegistry = checkNotNull(metricRegistry, "the metric registry must be not null");
        this.publishMetricName = KasperMetrics.name(getClass(), "publish");
        this.interceptorChainRegistry = new InterceptorChainRegistry<>();
    }

    /**
     * Build a Kasper event bus from the specified axon Cluster with default cluster selector
     *
     * @param cluster the cluster
     * @param metricRegistry the metric registry
     */
    public KasperEventBus(final MetricRegistry metricRegistry, final Cluster cluster) {
        super(new DefaultClusterSelector(checkNotNull(cluster)));
        this.currentPolicy = Policy.USER;
        this.optionalProcessorDownLatch = Optional.absent();
        this.metricRegistry = checkNotNull(metricRegistry, "the metric registry must be not null");
        this.publishMetricName = KasperMetrics.name(getClass(), "publish");
        this.interceptorChainRegistry = new InterceptorChainRegistry<>();
    }

    /**
     * Build an asynchronous Kasper event bus with the specified error handler
     *
     * @param errorHandler the error handler
     * @param metricRegistry the metric registry
     */
    public KasperEventBus(final MetricRegistry metricRegistry, final ErrorHandler errorHandler) {
        this(metricRegistry, Policy.ASYNCHRONOUS, errorHandler, new KasperProcessorDownLatch(metricRegistry));
    }

    public KasperEventBus(final MetricRegistry metricRegistry, final Policy busPolicy, final KasperProcessorDownLatch processorDownLatch) {
        this(metricRegistry, busPolicy, getDefaultErrorHandler(), processorDownLatch);
    }

    public KasperEventBus(
            final MetricRegistry metricRegistry,
            final Policy busPolicy,
            final ErrorHandler errorHandler,
            final KasperProcessorDownLatch processorDownLatch
    ) {
        super(getCluster(
                checkNotNull(busPolicy),
                checkNotNull(errorHandler),
                checkNotNull(metricRegistry),
                checkNotNull(processorDownLatch)
        ));
        this.currentPolicy = busPolicy;
        this.optionalProcessorDownLatch = Optional.of(processorDownLatch);
        this.metricRegistry = checkNotNull(metricRegistry, "the metric registry must be not null");
        this.publishMetricName = KasperMetrics.name(getClass(), "publish");
        this.interceptorChainRegistry = new InterceptorChainRegistry<>();
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

    @VisibleForTesting
    void publishToSuper(final EventMessage... messages) {
        checkNotNull(messages);
        try {
            super.publish(messages);
        } finally {
            metricRegistry.meter(publishMetricName).mark(messages.length);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void publish(final EventMessage... messages) {
        checkNotNull(messages);
        for (final EventMessage message : messages) {
            publish(
                    Objects.firstNonNull((Context) message.getMetaData().get(Context.METANAME), Contexts.empty()),
                    (Event) message.getPayload()
            );
        }
    }

    public void publish(final Context context, final Event event) {
        checkNotNull(context);
        checkNotNull(event);
        final Optional<InterceptorChain<Event, Void>> optionalRequestChain = getInterceptorChain(event.getClass());
        try {
            optionalRequestChain.get().next(event, context);
        } catch (final RuntimeException e) {
            LOGGER.error("Failed to publish event, <event={}> <context={}>", event, context, e);
            throw e;
        }
    }

    protected Optional<InterceptorChain<Event, Void>> getInterceptorChain(final Class<? extends Event> eventClass) {
        final Optional<InterceptorChain<Event, Void>> chainOptional = interceptorChainRegistry.get(eventClass);

        if (chainOptional.isPresent()) {
            return chainOptional;
        }

        final KasperEventBus thisEventBus = this;
        return interceptorChainRegistry.create(
                eventClass,
                new EventHandlerInterceptorFactory() {
                    @Override
                    protected Interceptor<Event, Void> getEventHandlerInterceptor() {
                        return new Interceptor<Event, Void>() {
                            @Override
                            public Void process(final Event event, final Context context, final InterceptorChain<Event, Void> chain) {
                                GenericEventMessage<Event> message = new GenericEventMessage<>(
                                        checkNotNull(event),
                                        ImmutableMap.<String, Object>builder().put(Context.METANAME, context).build()
                                );
                                thisEventBus.publishToSuper(message);
                                thisEventBus.noticePublicationHandlers(message);
                                return null;
                            }
                        };
                    }
                }
        );
    }

    // ------------------------------------------------------------------------

    public Optional<Runnable> getShutdownHook(){
        final KasperEventBus that = this;
        if (optionalProcessorDownLatch.isPresent()) {
            return Optional.<Runnable>of(new Runnable() {
                @Override
                public void run() {
                    LOGGER.info("Starting shutdown : Publication handlers");
                    for (final PublicationHandler handler : that.publicationHandlers) {
                        handler.shutdown();
                    }
                    LOGGER.info("Shutdown complete : Publication handlers");

                    LOGGER.info("Starting shutdown : Event Processing");
                    optionalProcessorDownLatch.get().await();
                    LOGGER.info("Shutdown complete : Event Processing");
                }
            });
        }
        return Optional.absent();
    }

    /**
     * Register an interceptor factory to the gateway
     *
     * @param interceptorFactory the query interceptor factory to register
     */
    public void register(final InterceptorFactory<Event, Void> interceptorFactory) {
        checkNotNull(interceptorFactory);
        LOGGER.info("Registering the query interceptor factory : " + interceptorFactory.getClass().getSimpleName());

        interceptorChainRegistry.register(interceptorFactory);
    }

}
