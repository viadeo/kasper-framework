package com.viadeo.kasper.event.saga.scheduling;

import com.viadeo.kasper.event.saga.Saga;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.lang.reflect.Method;

public interface SagaScheduler {

    String schedule(final Saga saga, final Method method, final String identifier, final Duration triggerDuration);

    String schedule(final Saga saga, final Method method, final String identifier, final DateTime triggerDateTime);

    void cancelSchedule(final Saga saga, final Method method, final String identifier, final String groupIdentifier);

}
