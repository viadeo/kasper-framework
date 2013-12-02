package com.viadeo.kasper.doc.element;

import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.domain.descriptor.AggregateDescriptor;
import com.viadeo.kasper.client.platform.domain.descriptor.RepositoryDescriptor;
import com.viadeo.kasper.doc.initializer.DocumentedElementVisitor;
import com.viadeo.kasper.event.Event;

import java.util.Collection;
import java.util.List;

import static com.viadeo.kasper.doc.element.DocumentedEventListener.DocumentedEvent;

public class DocumentedRepository extends AbstractDomainElement {

    private final DocumentedAggregate documentedAggregate;

    public DocumentedRepository(DocumentedDomain documentedDomain, RepositoryDescriptor repositoryDescriptor) {
        super(documentedDomain, DocumentedElementType.REPOSITORY, repositoryDescriptor.getReferenceClass());
        AggregateDescriptor aggregateDescriptor = repositoryDescriptor.getAggregateDescriptor();

        if (aggregateDescriptor.isRelation()) {
            documentedAggregate = new DocumentedRelation(documentedDomain, aggregateDescriptor);
        } else {
            documentedAggregate = new DocumentedConcept(documentedDomain, aggregateDescriptor);
        }
    }

    public LightDocumentedElement<DocumentedAggregate> getAggregate() {
        return documentedAggregate.getLightDocumentedElement();
    }

    @Override
    public LightDocumentedElement<DocumentedRepository> getLightDocumentedElement() {
        return new LightDocumentedElement<>(this);
    }

    @Override
    public void accept(DocumentedElementVisitor visitor) {
        documentedAggregate.accept(visitor);
        visitor.visit(this);
    }

    public static abstract class DocumentedAggregate extends AbstractPropertyDomainElement {

        private final Collection<LightDocumentedElement<DocumentedEvent>> sourceEvents;

        public DocumentedAggregate(DocumentedDomain domain, DocumentedElementType type, Class referenceClass, Collection<LightDocumentedElement<DocumentedEvent>> sourceEvents) {
            super(domain, type, referenceClass);
            this.sourceEvents = Lists.newArrayList(sourceEvents);
        }

        public Collection<LightDocumentedElement<DocumentedEvent>> getSourceEvents() {
            return sourceEvents;
        }

        @Override
        public void accept(DocumentedElementVisitor visitor) {
            for (LightDocumentedElement<DocumentedEvent> lightDocumentedElement : sourceEvents) {
                lightDocumentedElement.documentedElement.accept(visitor);
            }
        }
    }

    public static class DocumentedConcept extends DocumentedAggregate {

        private final List<LightDocumentedElement<DocumentedRelation>> sourceRelations;
        private final List<LightDocumentedElement<DocumentedRelation>> targetRelations;

        public DocumentedConcept(DocumentedDomain documentedDomain, Class referenceClass, Collection<LightDocumentedElement<DocumentedEvent>> sourceEvents) {
            super(documentedDomain, DocumentedElementType.CONCEPT, referenceClass, sourceEvents);
            this.sourceRelations = Lists.newArrayList();
            this.targetRelations = Lists.newArrayList();
        }

        public DocumentedConcept(DocumentedDomain documentedDomain, Class referenceClass) {
            this(documentedDomain, referenceClass, Lists.<LightDocumentedElement<DocumentedEvent>>newArrayList());
        }

        public DocumentedConcept(DocumentedDomain documentedDomain, AggregateDescriptor aggregateDescriptor) {
            this(documentedDomain
                    , aggregateDescriptor.getReferenceClass()
                    , toLightDocumentedEvents(documentedDomain, aggregateDescriptor.getSourceEventClasses())
            );
        }

        public void addSourceRelation(DocumentedRelation documentedRelation) {
            sourceRelations.add(documentedRelation.getLightDocumentedElement());
        }

        public void addTargetRelation(DocumentedRelation documentedRelation) {
            targetRelations.add(documentedRelation.getLightDocumentedElement());
        }

        public List<LightDocumentedElement<DocumentedRelation>> getSourceRelations() {
            return sourceRelations;
        }

        public List<LightDocumentedElement<DocumentedRelation>> getTargetRelations() {
            return targetRelations;
        }

        @Override
        public LightDocumentedElement<DocumentedConcept> getLightDocumentedElement() {
            return new LightDocumentedElement<>(this);
        }

        @Override
        public void accept(DocumentedElementVisitor visitor) {
            super.accept(visitor);
            visitor.visit(this);
        }
    }

    public static class DocumentedRelation extends DocumentedAggregate {

        private final DocumentedConcept sourceConcept;
        private final DocumentedConcept targetConcept;

        private String verb;
        private Boolean bidirectional;

        public DocumentedRelation(DocumentedDomain documentedDomain, AggregateDescriptor aggregateDescriptor) {
            this(documentedDomain
                    , aggregateDescriptor.getReferenceClass()
                    , new DocumentedConcept(documentedDomain, aggregateDescriptor.getSourceClass())
                    , new DocumentedConcept(documentedDomain, aggregateDescriptor.getTargetClass())
                    , toLightDocumentedEvents(documentedDomain, aggregateDescriptor.getSourceEventClasses())
            );
        }

        public DocumentedRelation(
                DocumentedDomain documentedDomain
                , Class referenceClass
                , DocumentedConcept sourceConcept
                , DocumentedConcept targetConcept
                , Collection<LightDocumentedElement<DocumentedEvent>> sourceEvents
        ) {
            super(documentedDomain, DocumentedElementType.RELATION, referenceClass, sourceEvents);
            this.sourceConcept = sourceConcept;
            this.targetConcept = targetConcept;
        }

        public String getVerb() {
            return verb;
        }

        public void setVerb(String verb) {
            this.verb = verb;
        }

        public void setBidirectional(Boolean bidirectional) {
            this.bidirectional = bidirectional;
        }

        public LightDocumentedElement<DocumentedConcept> getSourceConcept() {
            return sourceConcept.getLightDocumentedElement();
        }

        public LightDocumentedElement<DocumentedConcept> getTargetConcept() {
            return targetConcept.getLightDocumentedElement();
        }

        @Override
        public LightDocumentedElement<DocumentedRelation> getLightDocumentedElement() {
            return new LightDocumentedElement<DocumentedRelation>(this) {

                public String getSourceConceptName() {
                    return documentedElement.sourceConcept.getName();
                }

                public String getTargetConceptName() {
                    return documentedElement.targetConcept.getName();
                }
            };
        }

        @Override
        public void accept(DocumentedElementVisitor visitor) {
            super.accept(visitor);
            sourceConcept.accept(visitor);
            targetConcept.accept(visitor);
            visitor.visit(this);
        }
    }


    private static Collection<LightDocumentedElement<DocumentedEvent>> toLightDocumentedEvents(DocumentedDomain documentedDomain, Collection<Class<? extends Event>> eventClasses) {
        List<LightDocumentedElement<DocumentedEvent>> events = Lists.newArrayList();
        for (Class eventClass : eventClasses) {
            events.add(toDocumentedEvent(documentedDomain, eventClass).getLightDocumentedElement());
        }
        return events;
    }

    private static DocumentedEvent toDocumentedEvent(DocumentedDomain documentedDomain, Class eventClass) {
        return new DocumentedEvent(documentedDomain, null, eventClass);
    }
}
