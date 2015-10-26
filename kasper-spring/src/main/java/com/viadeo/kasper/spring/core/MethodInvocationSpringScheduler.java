// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viadeo.kasper.core.component.event.saga.SagaManager;
import com.viadeo.kasper.core.component.event.saga.step.quartz.MethodInvocationScheduler;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

/**
 * MethodInvocationScheduler implementation that delegates scheduling and triggering to a Quartz Scheduler.
 */
public class MethodInvocationSpringScheduler extends MethodInvocationScheduler implements SmartLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodInvocationSpringScheduler.class);

    // ------------------------------------------------------------------------

    public MethodInvocationSpringScheduler(final ObjectMapper mapper, final Scheduler scheduler, final SagaManager sagaManager) {
        this(mapper, scheduler, DEFAULT_GROUP_NAME, sagaManager);
    }

    public MethodInvocationSpringScheduler(
            final ObjectMapper mapper,
            final Scheduler scheduler,
            final String groupIdentifier,
            final SagaManager sagaManager
    ) {
        super(mapper, scheduler, groupIdentifier, sagaManager);
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean isAutoStartup() {
        return false;
    }

    @Override
    public void start() {
        this.initialize();
    }

    @Override
    public void stop() {
        try {
            this.shutdown();
        } catch (SchedulerException e) {
            LOGGER.error("Failed to shutdown the scheduler", e);
        }
    }

    @Override
    public void stop(Runnable callback) {
        stop();
    }

    @Override
    public boolean isRunning() {
        return isInitialized();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE - 1;
    }

}
