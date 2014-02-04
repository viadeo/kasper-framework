// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.cluster;

import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.EventListener;
import org.axonframework.eventhandling.async.AsynchronousCluster;
import org.axonframework.eventhandling.async.DefaultErrorHandler;
import org.axonframework.eventhandling.async.RetryPolicy;
import org.axonframework.eventhandling.async.SequentialPolicy;
import org.axonframework.unitofwork.DefaultUnitOfWorkFactory;
import org.axonframework.unitofwork.NoTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsynchronousClusterFactory implements ClusterFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsynchronousClusterFactory.class);

    private final Integer poolSize;
    private final Integer maximumPoolSize;
    private final Long keepAliveTime;
    private final TimeUnit timeUnit;

    public AsynchronousClusterFactory() {
        this(10, 100, 60L, TimeUnit.MINUTES);
    }

    public AsynchronousClusterFactory(
            final Integer poolSize,
            final Integer maximumPoolSize,
            final Long keepAliveTime,
            final TimeUnit timeUnit
    ){
        this.poolSize = poolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
    }

    @Override
    public Cluster createCluster(final String name) {
        return new AsynchronousCluster(
                name,
                new ThreadPoolExecutor(
                        poolSize,
                        maximumPoolSize,
                        keepAliveTime,
                        timeUnit,
                        new LinkedBlockingQueue<Runnable>()
                ),
                new DefaultUnitOfWorkFactory(new NoTransactionManager()),
                new SequentialPolicy(),
                new DefaultErrorHandler(RetryPolicy.proceed()) {
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
                }
        );
    }
}
