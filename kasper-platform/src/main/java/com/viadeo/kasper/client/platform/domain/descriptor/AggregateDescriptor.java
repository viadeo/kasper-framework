package com.viadeo.kasper.client.platform.domain.descriptor;

import com.google.common.collect.Lists;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.event.Event;

import java.util.Collection;
import java.util.List;

public class AggregateDescriptor implements Descriptor {

    private final Class<? extends AggregateRoot> aggregateClass;
    private final Class<? extends Concept> sourceClass;
    private final Class<? extends Concept> targetClass;
    private final Collection<Class<? extends Event>> sourceEventClasses;

    public AggregateDescriptor(Class<? extends AggregateRoot> aggregateClass, List<Class<? extends Event>> eventClasses) {
        this(aggregateClass, null, null, eventClasses);
    }

    public AggregateDescriptor(
              Class<? extends AggregateRoot> aggregateClass
            , Class<? extends Concept> sourceClass
            , Class<? extends Concept> targetClass
            , List<Class<? extends Event>> sourceEventClasses) {
        this.aggregateClass = aggregateClass;
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
        this.sourceEventClasses = Lists.newArrayList(sourceEventClasses);
    }

    public boolean isRelation(){
        return sourceClass != null && targetClass != null;
    }

    @Override
    public Class<? extends AggregateRoot> getReferenceClass() {
        return aggregateClass;
    }

    public Class<? extends Concept> getSourceClass() {
        return sourceClass;
    }

    public Class<? extends Concept> getTargetClass() {
        return targetClass;
    }

    public Collection<Class<? extends Event>> getSourceEventClasses() {
        return sourceEventClasses;
    }
}