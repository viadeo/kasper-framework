// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.google.common.base.Optional;
import com.viadeo.kasper.exception.KasperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;

/**
 * @author mglcel
 *
 * @param <T> The processed annotation type
 * @param <I> The required interface to be implemented by the processed class
 */
public abstract class SingletonAnnotationProcessor<T extends Annotation, I> implements AnnotationProcessor<T,I> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SingletonAnnotationProcessor.class);
	
    /**
     * Instances manager (optional)
     */
    private ComponentsInstanceManager instancesManager;

	// ------------------------------------------------------------------------	

    /**
     * Annotations are mandatory by default
     */
    @Override
    public boolean isAnnotationMandatory() {
        return true;
    }

	@SuppressWarnings("unchecked") // Must be controlled by child class (C)
	@Override
	public void process(final Class<?> clazz) {
		
		I instance = null;
		try {
			
			if (null != this.instancesManager) { // Try to retrieve from Spring bean
                final Optional<Object> optInstance = this.instancesManager.getInstanceFromClass(clazz);
                if (optInstance.isPresent()) {
                    instance = (I) optInstance.get();
                }
			}
			
			if (null == instance) {
				LOGGER.debug("Create new instance of " + clazz);
				instance = (I) clazz.newInstance();
				if (null != this.instancesManager) {
                    this.instancesManager.recordInstance(clazz, instance);
				}
			}
			
		} catch (final InstantiationException | IllegalAccessException e) {
			throw new KasperException("Unable to create instance of " + clazz.getName(), e);
		}

        this.process(clazz, instance);
	}
	
	protected abstract void process(final Class<?> clazz, I instance);
	
	// ------------------------------------------------------------------------

    /**
     * The components instance manager is set up by the root processor,
     * there is no need to provide it (but you can, it will not be overriden)
     *
     * @param instancesManager
     */
    public void setComponentsInstanceManager(final ComponentsInstanceManager instancesManager) {
        this.instancesManager = instancesManager;
    }

    /**
     * @return true if a components instance manager has already been set
     */
    public boolean hasComponentsInstanceManager() {
        return (null != this.instancesManager);
    }
	
}
