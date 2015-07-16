// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.saga;

public interface SagaIdReconciler {

    static final SagaIdReconciler NONE = new SagaIdReconciler() {
        @Override
        public Object reconcile(Object identifier) {
            return identifier;
        }
    };

    Object reconcile(Object identifier);
}
