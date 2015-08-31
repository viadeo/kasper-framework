// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga;

/**
 *  <code>SagaIdReconciler</code> can reconcile different types of identifiers in order to retrieve a particular <code>Saga</code>
 *  It's used for id conversions purpose.
 */
public interface SagaIdReconciler {

    static final SagaIdReconciler NONE = new SagaIdReconciler() {
        @Override
        public Object reconcile(Object identifier) {
            return identifier;
        }
    };

    Object reconcile(Object identifier);
}
