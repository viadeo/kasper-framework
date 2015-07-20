// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.bundle.descriptor.SagaDescriptor;
import com.viadeo.kasper.doc.initializer.DocumentedElementVisitor;

import java.io.Serializable;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DocumentedSaga extends AbstractDomainElement {

    private final List<DocumentedSagaStep> steps;

    public DocumentedSaga(final DocumentedDomain domain, final SagaDescriptor descriptor) {
        super(checkNotNull(domain), DocumentedElementType.SAGA, checkNotNull(descriptor).getReferenceClass());
        this.steps = Lists.newArrayList();

        for (SagaDescriptor.StepDescriptor stepDescriptor : descriptor.getStepDescriptors()) {
            this.steps.add(new DocumentedSagaStep(this, stepDescriptor));
        }
    }

    @Override
    public void accept(final DocumentedElementVisitor visitor) {
        visitor.visit(this);
        for (DocumentedSagaStep step : steps) {
            visitor.visit(step.getEvent());
        }
    }

    public List<DocumentedSagaStep> getSteps() {
        return steps;
    }

    public static class DocumentedSagaStep implements Serializable {

        private final DocumentedEventListener.DocumentedEvent documentedEvent;
        private final SagaDescriptor.StepDescriptor stepDescriptor;

        public DocumentedSagaStep(
                DocumentedSaga saga,
                SagaDescriptor.StepDescriptor stepDescriptor
        ) {
            this.stepDescriptor = stepDescriptor;
            this.documentedEvent = new DocumentedEventListener.DocumentedEvent(
                    saga,
                    stepDescriptor.getEventClass()
            );
        }

        public String getName() {
            return stepDescriptor.getName();
        }

        public List<String> getActions() {
            return stepDescriptor.getActions();
        }

        public DocumentedEventListener.DocumentedEvent getEvent() {
            return documentedEvent;
        }
    }
}
