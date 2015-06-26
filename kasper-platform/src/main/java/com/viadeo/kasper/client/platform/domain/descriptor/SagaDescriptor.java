// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.domain.descriptor;

import com.viadeo.kasper.event.saga.Saga;

public class SagaDescriptor implements KasperComponentDescriptor {

    private final Class<? extends Saga> sagaClass;

    public SagaDescriptor(final Class<? extends Saga> sagaClass) {
        this.sagaClass = sagaClass;
    }

    @Override
    public Class getReferenceClass() {
        return sagaClass;
    }
}
