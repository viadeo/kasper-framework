// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step;

import com.viadeo.kasper.event.saga.Saga;
import org.joda.time.DateTime;
import org.joda.time.Duration;

public interface Scheduler {

    void initialize();

    String schedule(final Class<? extends Saga> sagaClass, final String methodName, final String identifier, final Duration triggerDuration);

    String schedule(final Class<? extends Saga> sagaClass, final String methodName, final String identifier, final DateTime triggerDateTime);

    void cancelSchedule(final Class<? extends Saga> sagaClass, final String methodName, final String identifier);

}
