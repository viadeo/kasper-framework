// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static com.google.common.base.Preconditions.checkNotNull;

public class LightDocumentedElement<E extends AbstractElement> implements WithType {

    protected final E documentedElement;

    // ------------------------------------------------------------------------

    public LightDocumentedElement(final E documentedElement) {
        this.documentedElement = checkNotNull(documentedElement);
    }

    public String getType() {
        return documentedElement.getType();
    }

    public String getTypePlural() {
        return documentedElement.getTypePlural();
    }

    public String getName() {
        return documentedElement.getName();
    }

    public String getLabel() {
        return documentedElement.getLabel();
    }

    public String getDescription() {
        return documentedElement.getDescription();
    }

    public String getDomainName() {
        if (documentedElement instanceof AbstractDomainElement) {
            return ((AbstractDomainElement) documentedElement).getDomain().getName();
        }
        return null;
    }

    public String getURL() {
        return documentedElement.getURL();
    }

    @JsonIgnore
    public E getFullDocumentedElement() {
        return documentedElement;
    }

    @JsonIgnore
    public Class getReferenceClass() {
        return documentedElement.getReferenceClass();
    }

}
