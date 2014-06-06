package com.viadeo.kasper.client.platform.components.eventbus;

import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.EventListener;
import org.axonframework.eventhandling.async.*;
import org.axonframework.unitofwork.DefaultUnitOfWorkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

public class AsynchronousCluster extends org.axonframework.eventhandling.async.AsynchronousCluster implements SmartlifeCycleCluster {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsynchronousCluster.class);
    private KasperProcessorDownLatch processorDownLatch = new KasperProcessorDownLatch();

    public static class ErrorHandler extends DefaultErrorHandler {

        /**
         * Initializes the ErrorHandler, making it return the given <code>retryPolicy</code> when an error occurs.
         *
         * @param retryPolicy the policy to return on errors
         */
        public ErrorHandler(RetryPolicy retryPolicy) {
            super(retryPolicy);
        }

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
    }


    public AsynchronousCluster(String kasperClusterName, ThreadPoolExecutor threadPoolExecutor, DefaultUnitOfWorkFactory defaultUnitOfWorkFactory, SequentialPolicy sequentialPolicy, ErrorHandler errorHandler) {
        super(kasperClusterName, threadPoolExecutor, defaultUnitOfWorkFactory, sequentialPolicy, errorHandler);
    }


    @Override
    protected EventProcessor newProcessingScheduler(EventProcessor.ShutdownCallback shutDownCallback) {
        final EventProcessor eventProcessor = super.newProcessingScheduler(
                new KasperShutdownCallback(processorDownLatch, shutDownCallback)
        );
        processorDownLatch.process(eventProcessor);
        return eventProcessor;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        processorDownLatch.await();
        callback.run();
    }

    @Override
    public void start() {
     }

    @Override
    public void stop() {
        processorDownLatch.await();
        LOGGER.info("Shutdown complete");


    }

    @Override
    public boolean isRunning() {
        return true;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
