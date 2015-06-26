// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import com.viadeo.kasper.client.platform.domain.descriptor.SagaDescriptor;
import com.viadeo.kasper.doc.initializer.DocumentedElementVisitor;

public class DocumentedSaga extends AbstractDomainElement {

    public DocumentedSaga(final DocumentedDomain domain, final SagaDescriptor descriptor) {
        super(domain, DocumentedElementType.SAGA, descriptor.getReferenceClass());
    }

    @Override
    public void accept(final DocumentedElementVisitor visitor) {
        visitor.visit(this);
    }
}
