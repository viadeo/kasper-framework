package com.viadeo.kasper.event.saga.step;

import com.viadeo.kasper.event.saga.Saga;

import java.util.Set;

public interface StepResolver {
    Set<Step> resolve(Class<? extends Saga> sagaClass);
}
