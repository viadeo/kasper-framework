// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import org.axonframework.eventhandling.async.EventProcessor;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Personnalized Axon Event Processor shutdown callback
 *
 * Used to wrap the Kasper EventProcessor shutdown latching process
 */
public class KasperShutdownCallback implements EventProcessor.ShutdownCallback {

    private final EventProcessor.ShutdownCallback shutdownCallback;
    private final KasperProcessorDownLatch processorDownLatch;

    // ------------------------------------------------------------------------

    public KasperShutdownCallback(
            final KasperProcessorDownLatch processorDownLatch,
            final EventProcessor.ShutdownCallback shutdownCallback) {
        this.processorDownLatch = checkNotNull(processorDownLatch);
        this.shutdownCallback = checkNotNull(shutdownCallback);
    }

    // ------------------------------------------------------------------------

    @Override
    public void afterShutdown(final EventProcessor scheduler) {
        checkNotNull(scheduler);
        processorDownLatch.processDown(scheduler);
        shutdownCallback.afterShutdown(scheduler);
    }

}
