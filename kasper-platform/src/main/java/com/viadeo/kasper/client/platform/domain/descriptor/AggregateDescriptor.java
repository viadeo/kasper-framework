package com.viadeo.kasper.client.platform.domain.descriptor;

import com.google.common.collect.Lists;

import java.util.Collection;

public class AggregateDescriptor {

    private final Class aggregateClass;
    private final Class sourceClass;
    private final Class targetClass;
    private final Collection<Class> sourceEventClasses;

    public AggregateDescriptor(Class aggregateClass, Class... eventClasses) {
        this(aggregateClass, null, null, eventClasses);
    }

    public AggregateDescriptor(Class aggregateClass, Class sourceClass, Class targetClass, Class... sourceEventClasses) {
        this.aggregateClass = aggregateClass;
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
        this.sourceEventClasses = Lists.newArrayList(sourceEventClasses);
    }

    public boolean isRelation(){
        return sourceClass != null && targetClass != null;
    }

    public Class getReferenceClass() {
        return aggregateClass;
    }

    public Class getSourceClass() {
        return sourceClass;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public Collection<Class> getSourceEventClasses() {
        return sourceEventClasses;
    }
}