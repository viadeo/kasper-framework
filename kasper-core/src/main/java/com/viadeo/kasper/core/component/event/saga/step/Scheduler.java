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
     * @throws SchedulerException if an error occurs during the shutdown
     */
    void shutdown() throws SchedulerException;

    /**
     * indicates if the scheduler is initialized
     * @return
     */
    boolean isInitialized();

    /**
     * called in order to trigger a particular Saga method invocation
     *
     * @param sagaClass the saga class
     * @param methodName the method name
     * @param identifier the saga identifier
     * @param triggerDuration the delay at which the method will be triggered
     * @param endAfterExecution is the saga should end after scheduled method execution
     * @return the jobIdentifier of the scheduled operation
     */
    String schedule(Class<? extends Saga> sagaClass, String methodName, Object identifier, Duration triggerDuration, boolean endAfterExecution);

    /**
     * called in order to trigger a particular Saga method invocation
     *
     * @param sagaClass the saga class
     * @param methodName the method name
     * @param identifier the saga identifier
     * @param triggerDateTime the time at which the method will be triggered
     * @param endAfterExecution is the saga should end after scheduled method execution
     * @return the jobIdentifier of the scheduled operation
     */
    String schedule(Class<? extends Saga> sagaClass, String methodName, Object identifier, DateTime triggerDateTime, boolean endAfterExecution);

    /**
     * called to cancel a scheduled operation
     *
     * @param sagaClass the saga class
     * @param methodName the method name
     * @param identifier the saga identifier
     */
    void cancelSchedule(Class<? extends Saga> sagaClass, String methodName, Object identifier);

    /**
     * indicates if a particular method invocation is already scheduled.
     *
     * @param sagaClass the saga class
     * @param methodName the method name
     * @param identifier the saga identifier
     * @return true if scheduled
     */
    boolean isScheduled(final Class<? extends Saga> sagaClass, final String methodName, final Object identifier);

}
