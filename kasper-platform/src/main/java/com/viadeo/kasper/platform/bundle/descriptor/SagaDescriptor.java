// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.bundle.descriptor;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.core.component.event.saga.Saga;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class SagaDescriptor implements KasperComponentDescriptor {

    private final Class<? extends Saga> sagaClass;
    private final List<StepDescriptor> stepDescriptors;

    public SagaDescriptor(final Class<? extends Saga> sagaClass, final List<StepDescriptor> stepDescriptors) {
        this.sagaClass = checkNotNull(sagaClass);
        this.stepDescriptors = checkNotNull(stepDescriptors);
    }

    @Override
    public Class getReferenceClass() {
        return sagaClass;
    }

    public List<StepDescriptor> getStepDescriptors() {
        return stepDescriptors;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sagaClass, stepDescriptors);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SagaDescriptor other = (SagaDescriptor) obj;
        return Objects.equal(this.sagaClass, other.sagaClass) && Objects.equal(this.stepDescriptors, other.stepDescriptors);
    }

    public static class StepDescriptor implements KasperComponentDescriptor {

        private final String name;
        private final Class<? extends Event> eventClass;
        private final List<String> actions;

        public StepDescriptor(String name, Class<? extends Event> eventClass, List<String> actions) {
            this.name = checkNotNull(name);
            this.eventClass = checkNotNull(eventClass);
            this.actions = Lists.newArrayList(checkNotNull(actions));
        }

        @Override
        public Class getReferenceClass() {
            return eventClass;
        }

        public String getName() {
            return name;
        }

        public Class<? extends Event> getEventClass() {
            return eventClass;
        }

        public List<String> getActions() {
            return actions;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, eventClass, actions);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final StepDescriptor other = (StepDescriptor) obj;
            return Objects.equal(this.name, other.name) && Objects.equal(this.eventClass, other.eventClass) && Objects.equal(this.actions, other.actions);
        }
    }
}
