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

/**
 * Interface describing the scheduler Step of a Saga
 */
public interface Scheduler {

    void initialize();

    String schedule(Class<? extends Saga> sagaClass, String methodName, Object identifier, Duration triggerDuration);

    String schedule(Class<? extends Saga> sagaClass, String methodName, Object identifier, DateTime triggerDateTime);

    void cancelSchedule(Class<? extends Saga> sagaClass, String methodName, Object identifier);

    boolean isScheduled(final Class<? extends Saga> sagaClass, final String methodName, final Object identifier);

}
