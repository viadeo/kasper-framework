// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.factory;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.component.event.saga.Saga;

/**
 * Provide a <code>SagaFactory</code> for a given <code>Saga</code>.
 */
public interface SagaFactoryProvider {

    SagaFactory getOrCreate(final Saga saga);

    <SAGA extends Saga> Optional<SagaFactory> get(final Class<SAGA> sagaClass);

}
