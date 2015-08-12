// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.factory;

import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.exception.SagaInstantiationException;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.exception.SagaInstantiationException;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.exception.SagaInstantiationException;

/**
 * Interface describing a mechanism that creates implementations of a Saga.
 */
public interface  SagaFactory {

    /**
     * Instantiate a <code>Saga</code>
     *
     * @param identifier
     * @param sagaClass
     * @param <SAGA>
     * @return the instantiated <code>Saga</code>
     * @throws SagaInstantiationException
     */
    <SAGA extends Saga> SAGA create(Object identifier, Class<SAGA> sagaClass) throws SagaInstantiationException;

}
