package com.viadeo.kasper.doc.element;

public abstract class AbstractDomainElement extends AbstractElement {

    private final DocumentedDomain domain;

    public AbstractDomainElement(DocumentedDomain domain, DocumentedElementType type, Class referenceClass) {
        super(type, referenceClass);
        this.domain = domain;
    }

    public LightDocumentedElement<DocumentedDomain> getDomain() {
        return domain.getLightDocumentedElement();
    }

    @Override
    public String getURL() {
        return String.format("/%s/%s/%s/%s"
                , domain.getType()
                , domain.getName()
                , getType()
                , getName()
        );
    }
}
