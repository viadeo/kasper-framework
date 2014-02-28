// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import org.axonframework.eventhandling.async.EventProcessor;

public class KasperShutdownCallback implements EventProcessor.ShutdownCallback {

    private final EventProcessor.ShutdownCallback shutdownCallback;
    private final ProcessorDownLatch processorDownLatch;

    public KasperShutdownCallback(
            final ProcessorDownLatch processorDownLatch,
            final EventProcessor.ShutdownCallback shutdownCallback
    ) {
        this.processorDownLatch = processorDownLatch;
        this.shutdownCallback = shutdownCallback;
    }

    @Override
    public void afterShutdown(final EventProcessor scheduler) {
        processorDownLatch.processDown(scheduler);
        shutdownCallback.afterShutdown(scheduler);
    }
}
