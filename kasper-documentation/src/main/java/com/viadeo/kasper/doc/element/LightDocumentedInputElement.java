// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import java.util.List;

public class LightDocumentedInputElement<E extends AbstractPropertyDomainElement> extends LightDocumentedElement<E> {


    public LightDocumentedInputElement(E documentedElement) {
        super(documentedElement);
    }

    public List<String> getAliases() {
        return documentedElement.getAliases();
    }

}
