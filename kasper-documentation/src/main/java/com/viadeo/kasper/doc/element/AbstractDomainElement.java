// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import com.viadeo.kasper.security.annotation.XKasperPublic;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractDomainElement extends AbstractElement {

    private final DocumentedDomain domain;
    private final boolean publicAccess;

    // ------------------------------------------------------------------------

    public AbstractDomainElement(final DocumentedDomain domain, final DocumentedElementType type, final Class referenceClass) {
        super(checkNotNull(type), checkNotNull(referenceClass));
        this.domain = checkNotNull(domain);

        this.publicAccess = referenceClass.getAnnotation(XKasperPublic.class) != null;

    }

    // ------------------------------------------------------------------------

    public LightDocumentedElement<DocumentedDomain> getDomain() {
        return domain.getLightDocumentedElement();
    }

    public boolean isPublicAccess() {
        return publicAccess;
    }

    @Override
    public String getURL() {
        return String.format(
                "/%s/%s/%s/%s",
                domain.getType(),
                domain.getLabel(),
                getType(),
                getName()
        );
    }

}
