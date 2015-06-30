// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

import com.viadeo.kasper.event.saga.exception.SagaInstantiationException;

/**
 * Interface describing a mechanism that creates implementations of a Saga.
 */
public interface  SagaFactory {

    <SAGA extends Saga> SAGA create(Object identifier, Class<SAGA> sagaClass) throws SagaInstantiationException;

}
