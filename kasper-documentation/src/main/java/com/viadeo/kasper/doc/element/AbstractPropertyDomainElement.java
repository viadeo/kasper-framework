// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import com.viadeo.kasper.doc.nodes.DocumentedBean;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractPropertyDomainElement extends AbstractDomainElement {

    private final DocumentedBean properties;

    // ------------------------------------------------------------------------

    public AbstractPropertyDomainElement(final DocumentedDomain domain,
                                         final DocumentedElementType type,
                                         final Class referenceClass){
        super(checkNotNull(domain), checkNotNull(type), checkNotNull(referenceClass));
        this.properties = new DocumentedBean(referenceClass);
    }

    // ------------------------------------------------------------------------

    public DocumentedBean getProperties() {
        return properties;
    }

}
