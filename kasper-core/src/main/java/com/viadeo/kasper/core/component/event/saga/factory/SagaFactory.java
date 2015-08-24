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
     * @param identifier the saga identifier
     * @param sagaClass the saga class
     * @param <SAGA> the saga instance type
     * @return the instantiated <code>Saga</code>
     * @throws SagaInstantiationException if an error occurs during the instantiation
     */
    <SAGA extends Saga> SAGA create(Object identifier, Class<SAGA> sagaClass) throws SagaInstantiationException;

}
