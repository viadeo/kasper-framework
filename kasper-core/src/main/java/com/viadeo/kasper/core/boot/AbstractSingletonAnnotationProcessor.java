// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import java.lang.annotation.Annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.viadeo.kasper.exception.KasperRuntimeException;

/**
 * @author mglcel
 *
 * @param <T> The processed annotation type
 * @param <I> The required interface to be implemented by the processed class
 */
public abstract class AbstractSingletonAnnotationProcessor<T extends Annotation, I> implements IAnnotationProcessor<T,I>, ApplicationContextAware {

	private static Logger LOGGER = LoggerFactory.getLogger(AbstractSingletonAnnotationProcessor.class);
	
	/** 
	 * Only used if Spring context available in order to reuse injected processor instances 
	 */
	private ApplicationContext context;	

	// ------------------------------------------------------------------------	
	
	@SuppressWarnings("unchecked") // Must be controlled by child class (C)
	@Override
	public void process(final Class<?> clazz) {
		
		I instance = null;
		try {
			
			if ((null != context)) { // Try to retrieve from Spring bean
				try {
					instance = (I) context.getBean(clazz);
					LOGGER.debug("Use bounded spring bean for " + clazz);
				} catch (final NoSuchBeanDefinitionException e) {
					// Ignore
				}
			}
			
			if (null == instance) {
				LOGGER.debug("Create new instance of " + clazz);
				instance = (I) clazz.newInstance();
				if (null != this.context) {
					final ConfigurableBeanFactory cfb = (ConfigurableBeanFactory) this.context.getAutowireCapableBeanFactory();
					((AutowireCapableBeanFactory) cfb).autowireBean(instance);
					cfb.registerSingleton(clazz.getSimpleName(), instance);
				}
			}
			
		} catch (final InstantiationException e) {
			throw new KasperRuntimeException("Unable to create instance of " + clazz.getName(), e);
		} catch (final IllegalAccessException e) {
			throw new KasperRuntimeException("Unable to create instance of " + clazz.getName(), e);
		}
		
		this.process(clazz, instance);
	}
	
	abstract void process(Class<?> clazz, I instance);
	
	// ------------------------------------------------------------------------
	
	@Override
	public void setApplicationContext(final ApplicationContext context)
			throws BeansException {
		this.context = context;
	}
	
}
