// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.doc.initializer.DocumentedElementVisitor;
import com.viadeo.kasper.platform.bundle.descriptor.AggregateDescriptor;
import com.viadeo.kasper.platform.bundle.descriptor.RepositoryDescriptor;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.doc.element.DocumentedEventListener.DocumentedEvent;

public class DocumentedRepository extends AbstractDomainElement {

    private final DocumentedAggregate documentedAggregate;

    // ------------------------------------------------------------------------

    public abstract static class DocumentedAggregate extends AbstractPropertyDomainElement {

        private final Collection<LightDocumentedElement<DocumentedEvent>> sourceEvents;
        private final DocumentedRepository documentedRepository;

        public DocumentedAggregate(final DocumentedDomain domain,
                                   final DocumentedElementType type,
                                   final Class referenceClass,
                                   final Collection<LightDocumentedElement<DocumentedEvent>> sourceEvents,
                                   final DocumentedRepository documentedRepository
        ) {
            super(domain, type, referenceClass);
            this.documentedRepository = documentedRepository;
            this.sourceEvents = Lists.newArrayList(sourceEvents);
        }

        public Collection<LightDocumentedElement<DocumentedEvent>> getSourceEvents() {
            return sourceEvents;
        }

        public LightDocumentedElement<DocumentedRepository> getRepository() {
            return documentedRepository == null ? null : documentedRepository.getLightDocumentedElement();
        }

        @Override
        public void accept(final DocumentedElementVisitor visitor) {
            for (final LightDocumentedElement<DocumentedEvent> lightDocumentedElement : sourceEvents) {
                lightDocumentedElement.documentedElement.accept(visitor);
            }
        }
    }

    // ------------------------------------------------------------------------

    public static class DocumentedConcept extends DocumentedAggregate {

        private final List<LightDocumentedElement<DocumentedRelation>> sourceRelations;
        private final List<LightDocumentedElement<DocumentedRelation>> targetRelations;
        private final List<LightDocumentedElement<DocumentedConcept>> componentConcepts;

        public DocumentedConcept(final DocumentedDomain documentedDomain,
                                 final Class referenceClass,
                                 final Collection<LightDocumentedElement<DocumentedEvent>> sourceEvents,
                                 final DocumentedRepository documentedRepository
        ) {
            super(documentedDomain, DocumentedElementType.CONCEPT, referenceClass, sourceEvents, documentedRepository);
            this.sourceRelations = Lists.newArrayList();
            this.targetRelations = Lists.newArrayList();
            this.componentConcepts = Lists.newArrayList();
        }

        public DocumentedConcept(
                final DocumentedDomain documentedDomain,
                final Class referenceClass,
                final DocumentedRepository documentedRepository
        ) {
            this(documentedDomain, referenceClass, Lists.<LightDocumentedElement<DocumentedEvent>>newArrayList(), documentedRepository);
        }

        public DocumentedConcept(
                final DocumentedDomain documentedDomain,
                final AggregateDescriptor aggregateDescriptor,
                final DocumentedRepository documentedRepository
        ) {
            this(documentedDomain,
                 aggregateDescriptor.getReferenceClass(),
                 toLightDocumentedEvents(documentedDomain, aggregateDescriptor.getSourceEventClasses()),
                 documentedRepository
            );
        }

        public void addSourceRelation(final DocumentedRelation documentedRelation) {
            sourceRelations.add(documentedRelation.getLightDocumentedElement());
        }

        public void addTargetRelation(final DocumentedRelation documentedRelation) {
            targetRelations.add(documentedRelation.getLightDocumentedElement());
        }

        public void addComponentConcept(final DocumentedConcept documentedConcept) {
            componentConcepts.add(documentedConcept.getLightDocumentedElement());
        }

        public List<LightDocumentedElement<DocumentedRelation>> getSourceRelations() {
            return sourceRelations;
        }

        public List<LightDocumentedElement<DocumentedRelation>> getTargetRelations() {
            return targetRelations;
        }

        public List<LightDocumentedElement<DocumentedConcept>> getComponentConcepts() {
            return componentConcepts;
        }

        @Override
        public LightDocumentedElement<DocumentedConcept> getLightDocumentedElement() {
            return new LightDocumentedElement<>(this);
        }

        @Override
        public void accept(final DocumentedElementVisitor visitor) {
            super.accept(visitor);
            visitor.visit(this);
        }
    }

    // ------------------------------------------------------------------------

    public static class DocumentedRelation extends DocumentedAggregate {

        private final DocumentedConcept sourceConcept;
        private final DocumentedConcept targetConcept;

        private String verb;
        private Boolean bidirectional;

        public DocumentedRelation(final DocumentedDomain documentedDomain,
                                  final AggregateDescriptor aggregateDescriptor,
                                  final DocumentedRepository documentedRepository
        ) {
            this(documentedDomain,
                 aggregateDescriptor.getReferenceClass(),
                 new DocumentedConcept(documentedDomain, aggregateDescriptor.getSourceClass(), documentedRepository),
                 new DocumentedConcept(documentedDomain, aggregateDescriptor.getTargetClass(), documentedRepository),
                 toLightDocumentedEvents(documentedDomain, aggregateDescriptor.getSourceEventClasses()),
                 documentedRepository
            );
        }

        public DocumentedRelation(final DocumentedDomain documentedDomain,
                                  final Class referenceClass,
                                  final DocumentedConcept sourceConcept,
                                  final DocumentedConcept targetConcept,
                                  final Collection<LightDocumentedElement<DocumentedEvent>> sourceEvents,
                                  final DocumentedRepository documentedRepository
        ) {
            super(documentedDomain, DocumentedElementType.RELATION, referenceClass, sourceEvents, documentedRepository);
            this.sourceConcept = sourceConcept;
            this.targetConcept = targetConcept;
        }

        public String getVerb() {
            return verb;
        }

        public void setVerb(final String verb) {
            this.verb = verb;
        }

        public void setBidirectional(final Boolean bidirectional) {
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
        public void accept(final DocumentedElementVisitor visitor) {
            super.accept(visitor);
            sourceConcept.accept(visitor);
            targetConcept.accept(visitor);
            visitor.visit(this);
        }
    }

    // ------------------------------------------------------------------------

    public DocumentedRepository(final DocumentedDomain documentedDomain, final RepositoryDescriptor repositoryDescriptor) {
        super(
            checkNotNull(documentedDomain),
            DocumentedElementType.REPOSITORY,
            checkNotNull(repositoryDescriptor).getReferenceClass()
        );

        final AggregateDescriptor aggregateDescriptor = repositoryDescriptor.getAggregateDescriptor();
        if (aggregateDescriptor.isRelation()) {
            documentedAggregate = new DocumentedRelation(documentedDomain, aggregateDescriptor, this);
        } else {
            documentedAggregate = new DocumentedConcept(documentedDomain, aggregateDescriptor, this);
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public LightDocumentedElement<DocumentedRepository> getLightDocumentedElement() {
        return new LightDocumentedElement<>(this);
    }

    @Override
    public void accept(final DocumentedElementVisitor visitor) {
        documentedAggregate.accept(visitor);
        visitor.visit(this);
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public LightDocumentedElement<DocumentedAggregate> getAggregate() {
        return documentedAggregate.getLightDocumentedElement();
    }

    // ------------------------------------------------------------------------

    private static Collection<LightDocumentedElement<DocumentedEvent>> toLightDocumentedEvents(
            final DocumentedDomain documentedDomain,
            final Collection<Class<? extends Event>> eventClasses) {

        final List<LightDocumentedElement<DocumentedEvent>> events = Lists.newArrayList();
        for (final Class eventClass : eventClasses) {
            events.add(toDocumentedEvent(documentedDomain, eventClass).getLightDocumentedElement());
        }
        return events;
    }

    private static DocumentedEvent toDocumentedEvent(final DocumentedDomain documentedDomain,
                                                     final Class eventClass) {
        return new DocumentedEvent(documentedDomain, null, eventClass);
    }

}
