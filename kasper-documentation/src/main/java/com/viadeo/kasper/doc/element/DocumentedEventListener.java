package com.viadeo.kasper.doc.element;

import com.viadeo.kasper.client.platform.domain.descriptor.EventListenerDescriptor;
import com.viadeo.kasper.doc.initializer.DocumentedElementVisitor;

public class DocumentedEventListener extends AbstractDomainElement {

    private final DocumentedEvent documentedEvent;

    public DocumentedEventListener(DocumentedDomain documentedDomain, EventListenerDescriptor eventListenerDescriptor) {
        super(documentedDomain, DocumentedElementType.EVENT_LISTENER, eventListenerDescriptor.getReferenceClass());
        documentedEvent = new DocumentedEvent(documentedDomain, this, eventListenerDescriptor.getEventClass());
    }

    public LightDocumentedElement<DocumentedEvent> getEvent() {
        return documentedEvent.getLightDocumentedElement();
    }

    public String getEventName() {
        return documentedEvent.getName();
    }

    @Override
    public LightDocumentedElement<DocumentedEventListener> getLightDocumentedElement() {
        return new LightDocumentedElement<>(this);
    }

    @Override
    public void accept(DocumentedElementVisitor visitor) {
        documentedEvent.accept(visitor);
        visitor.visit(this);
    }

    public static class DocumentedEvent extends AbstractPropertyDomainElement {

        private final DocumentedEventListener documentedEventListener;

        private String action;

        public DocumentedEvent(DocumentedDomain documentedDomain, DocumentedEventListener documentedEventListener, Class eventClass) {
            super(documentedDomain, DocumentedElementType.EVENT, eventClass);
            this.documentedEventListener = documentedEventListener;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public LightDocumentedElement<DocumentedEventListener> getEventListener() {
            return documentedEventListener != null ? documentedEventListener.getLightDocumentedElement() : null;
        }

        @Override
        public LightDocumentedElement<DocumentedEvent> getLightDocumentedElement() {
            return new LightDocumentedElement<>(this);
        }

        @Override
        public void accept(DocumentedElementVisitor visitor) {
            visitor.visit(this);
        }
    }
}
