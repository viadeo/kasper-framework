// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.bundle.descriptor;

import com.google.common.collect.Lists;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.api.component.event.Event;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class AggregateDescriptor implements KasperComponentDescriptor {

    private final Class<? extends AggregateRoot> aggregateClass;
    private final Class<? extends Concept> sourceClass;
    private final Class<? extends Concept> targetClass;
    private final Collection<Class<? extends Event>> sourceEventClasses;

    // ------------------------------------------------------------------------

    public AggregateDescriptor(final Class<? extends AggregateRoot> aggregateClass,
                               final List<Class<? extends Event>> eventClasses) {
        this(checkNotNull(aggregateClass), null, null, checkNotNull(eventClasses));
    }

    public AggregateDescriptor(final Class<? extends AggregateRoot> aggregateClass,
                               final Class<? extends Concept> sourceClass,
                               final Class<? extends Concept> targetClass,
                               final List<Class<? extends Event>> sourceEventClasses) {
        this.aggregateClass = checkNotNull(aggregateClass);
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
        this.sourceEventClasses = Lists.newArrayList(checkNotNull(sourceEventClasses));
    }

    // ------------------------------------------------------------------------

    public boolean isRelation() {
        return (null != sourceClass) && (null != targetClass);
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
