// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.step;

import com.viadeo.kasper.core.component.event.saga.Saga;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.quartz.SchedulerException;

/**
 * Interface describing the scheduler Step of a Saga
 */
public interface Scheduler {

    /**
     * initialize the <code>Scheduler</code>
     */
    void initialize();

    /**
     * shutdown the <code>Scheduler</code>
     *
     * @throws SchedulerException
     */
    void shutdown() throws SchedulerException;

    /**
     * called in order to trigger a particular Saga method invocation
     *
     * @param sagaClass
     * @param methodName
     * @param identifier
     * @param triggerDuration
     * @return the jobIdentifier of the scheduled operation
     */
    String schedule(Class<? extends Saga> sagaClass, String methodName, Object identifier, Duration triggerDuration);

    /**
     * called in order to trigger a particular Saga method invocation
     *
     * @param sagaClass
     * @param methodName
     * @param identifier
     * @param triggerDateTime
     * @return the jobIdentifier of the scheduled operation
     */
    String schedule(Class<? extends Saga> sagaClass, String methodName, Object identifier, DateTime triggerDateTime);

    /**
     * called to cancel a scheduled operation
     *
     * @param sagaClass
     * @param methodName
     * @param identifier
     */
    void cancelSchedule(Class<? extends Saga> sagaClass, String methodName, Object identifier);

    /**
     * indicates if a particular method invocation is already scheduled.
     *
     * @param sagaClass
     * @param methodName
     * @param identifier
     * @return true if scheduled
     */
    boolean isScheduled(final Class<? extends Saga> sagaClass, final String methodName, final Object identifier);

}
