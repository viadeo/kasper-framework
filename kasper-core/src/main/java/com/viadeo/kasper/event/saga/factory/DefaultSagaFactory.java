// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.factory;

import com.viadeo.kasper.event.saga.Saga;
import com.viadeo.kasper.event.saga.exception.SagaInstantiationException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

/**
 * Default implementation of a SagaFactory using @link org.springframework.context.ApplicationContext
 */
public class DefaultSagaFactory implements SagaFactory {

    private final AutowireCapableBeanFactory beanFactory;

    public DefaultSagaFactory(final ApplicationContext applicationContext) {
        beanFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    @Override
    public <SAGA extends Saga> SAGA create(final Object identifier, final Class<SAGA> sagaClass) {
        try {
            return (SAGA) beanFactory.autowire(sagaClass, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, Boolean.TRUE);
        } catch (final UnsatisfiedDependencyException e) {
            throw new SagaInstantiationException(String.format("Error instantiating saga of '%s'", sagaClass.getName()), e);
        }
    }

}
