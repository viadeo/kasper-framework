// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.google.common.base.Optional;

/**
 * A Kasper components instance manager (factory and registry)
 *
 * Two strategies can be implemented :
 *
 * - you are a factory and creates missing instances on request (cached)
 *     -> in this case you can also decide to register instances that you will
 *        be provided with, or just refuse to register multiple instances of the same class
 * - you are just a registry and do not create missing instances
 *
 */
public interface ComponentsInstanceManager {

    /**
     * Given a class, return an instance of the corresponding component class
     *
     * Depending on your instances creation strategy, you can decide to build a new
     * instance on request or just to wait for new instances to register and so not
     * acting as a factory but a simple registry
     *
     * @param clazz the class for which you have to provide an instance
     * @return an option instance of this class
     */
    <E> Optional<E> getInstanceFromClass(Class<? extends E> clazz);

    /**
     * The user of this manager can create it's own instance (forced by its creator for instance)
     * You have to record this instance, or raise some error if you do not support overriding
     *
     * @param clazz the class to use for recording the instance
     * @param objInstance the instance to be recorded
     */
    void recordInstance(Class clazz, Object objInstance);

}
