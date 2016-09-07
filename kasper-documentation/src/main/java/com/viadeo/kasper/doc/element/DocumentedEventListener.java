// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
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
