// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.factory;

import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.exception.SagaInstantiationException;

/**
 * Default implementation of a SagaFactory
 */
public class DefaultSagaFactory implements SagaFactory {

    @Override
    public <SAGA extends Saga> SAGA create(final Object identifier, final Class<SAGA> sagaClass) {
        try {
        return sagaClass.newInstance();
    } catch (final InstantiationException | IllegalAccessException e) {
            throw new SagaInstantiationException(String.format(
                        "Error instantiating saga of '%s'",
                        sagaClass.getName()
                    ), e);
        }
    }

}
