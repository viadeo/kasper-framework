// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

public class DefaultSagaFactory implements SagaFactory {

    private final AutowireCapableBeanFactory beanFactory;

    public DefaultSagaFactory(ApplicationContext applicationContext) {
        beanFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    @Override
    public <SAGA extends Saga> SAGA create(Object identifier, Class<SAGA> sagaClass) {
        try {
            return (SAGA) beanFactory.autowire(sagaClass, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, Boolean.TRUE);
        } catch (UnsatisfiedDependencyException e) {
            throw new SagaInstantitationException(String.format("Error instantiating saga of '%s'", sagaClass.getName()), e);
        }
    }
}
