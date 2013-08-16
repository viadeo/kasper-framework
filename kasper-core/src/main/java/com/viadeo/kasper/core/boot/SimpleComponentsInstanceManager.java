// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.core.boot;

import com.google.common.base.Optional;
import com.google.common.collect.MutableClassToInstanceMap;
import com.viadeo.kasper.exception.KasperException;

/**
 * Base implementation for a components instance manager based on a simple map
 */
public class SimpleComponentsInstanceManager implements ComponentsInstanceManager {

    private final MutableClassToInstanceMap<Object> instances;

    private static final String ERROR_INSTANCE = "Unable to create a new instance of class %s";
    private static final String ALREADY_REGISTERED = "An instance of class %s has already been registered";

    // ------------------------------------------------------------------------

    public SimpleComponentsInstanceManager() {
        this.instances = MutableClassToInstanceMap.create();
    }

    // ------------------------------------------------------------------------

    /**
     * @param clazz the class for which you have to provide an instance
     * @return the corresponding bean (optional)
     */
    @Override
    public <E> Optional<E> getInstanceFromClass(final Class<? extends E> clazz) {
        E objInstance;

        if (instances.containsKey(clazz)) {
            objInstance = (E) this.instances.get(clazz);
        } else {
            try {
                objInstance = clazz.newInstance();
            } catch (final InstantiationException|IllegalAccessException e) {
                throw new KasperException(String.format(ERROR_INSTANCE, clazz), e);
            }
            this.instances.put(clazz, objInstance);
        }

        return Optional.fromNullable(objInstance);
    }

    /**
     * @param clazz the class to use for recording the instance
     * @param objInstance the instance to be recorded
     */
    @Override
    public void recordInstance(final Class<?> clazz, final Object objInstance) {

        if (this.instances.containsKey(clazz) || this.instances.containsKey(objInstance.getClass())) {
            throw new KasperException(String.format(ALREADY_REGISTERED, clazz));
        } else {
            this.instances.put(clazz, objInstance);
        }

    }

}

