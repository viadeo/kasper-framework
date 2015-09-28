// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.response.KasperResponse;
import com.viadeo.kasper.doc.initializer.DocumentedElementVisitor;
import com.viadeo.kasper.doc.nodes.DocumentedBean;
import com.viadeo.kasper.platform.bundle.descriptor.EventListenerDescriptor;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DocumentedEventListener extends AbstractDomainElement {

    private final List<DocumentedEvent> documentedEvents;

    public static class DocumentedEvent extends AbstractPropertyDomainElement {

        private static final LinkedMultiValueMap<Class, LightDocumentedElement> LISTENERS_BY_EVENTS = new LinkedMultiValueMap<>();

        private String action;
        private final DocumentedBean response;

        public DocumentedEvent(final AbstractDomainElement documentedEventListener, final Class eventClass) {
            this(DocumentedDomain.UNKNOWN_DOMAIN, documentedEventListener, eventClass);
        }

        public DocumentedEvent(final DocumentedDomain documentedDomain,
                               final AbstractDomainElement documentedEventListener,
                               final Class eventClass) {
            super(documentedDomain, DocumentedElementType.EVENT, eventClass);

            if (null != documentedEventListener) {
                LISTENERS_BY_EVENTS.add(eventClass, documentedEventListener.getLightDocumentedElement());
            }
            this.response = new DocumentedBean(KasperResponse.class);
        }

        public String getAction() {
            return action;
        }

        public void setAction(final String action) {
            this.action = action;
        }

        public void setDomain(final DocumentedDomain domain) {
            this.domain = checkNotNull(domain);
        }

        public List<LightDocumentedElement> getEventListeners() {
            final List<LightDocumentedElement> eventListeners = LISTENERS_BY_EVENTS.get(getReferenceClass());
            return eventListeners != null ? eventListeners : null;
        }

        public DocumentedBean getResponse() {
            return response;
        }

        @Override
        public LightDocumentedElement<DocumentedEvent> getLightDocumentedElement() {
            return new LightDocumentedInputElement<>(this);
        }

        @Override
        public void accept(final DocumentedElementVisitor visitor) {
            visitor.visit(this);
        }
    }

    // ------------------------------------------------------------------------

    public DocumentedEventListener(final DocumentedDomain documentedDomain,
                                   final EventListenerDescriptor eventListenerDescriptor) {
        super(
                checkNotNull(documentedDomain),
                DocumentedElementType.EVENT_LISTENER,
                checkNotNull(eventListenerDescriptor).getReferenceClass()
        );

        documentedEvents = Lists.newArrayList();

        for (Class<? extends Event> eventClass : eventListenerDescriptor.getEventClasses()) {
            documentedEvents.add(new DocumentedEvent(this, eventClass));
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public LightDocumentedElement<DocumentedEventListener> getLightDocumentedElement() {
        return new LightDocumentedElement<>(this);
    }

    @Override
    public void accept(final DocumentedElementVisitor visitor) {
        for (DocumentedEvent documentedEvent : documentedEvents) {
            documentedEvent.accept(visitor);
        }
        visitor.visit(this);
    }

    // ------------------------------------------------------------------------

    public List<LightDocumentedElement<DocumentedEvent>> getEvents() {
        final List<LightDocumentedElement<DocumentedEvent>> lightDocumentedEvents = Lists.newArrayList();
        for (DocumentedEvent documentedEvent : documentedEvents) {
            lightDocumentedEvents.add(documentedEvent.getLightDocumentedElement());
        }
        return lightDocumentedEvents;
    }

}
