package com.viadeo.kasper.doc.element;

import com.viadeo.kasper.doc.nodes.DocumentedBean;

public abstract class AbstractPropertyDomainElement extends AbstractDomainElement {

    private final DocumentedBean properties;

    public AbstractPropertyDomainElement(DocumentedDomain domain, DocumentedElementType type, Class referenceClass){
        super(domain, type, referenceClass);
        this.properties = new DocumentedBean(referenceClass);
    }

    public DocumentedBean getProperties() {
        return properties;
    }
}
