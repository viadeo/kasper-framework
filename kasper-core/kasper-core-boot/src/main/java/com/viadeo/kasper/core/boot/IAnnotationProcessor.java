// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.core.boot;

import java.lang.annotation.Annotation;

/**
 *
 * Annotated classes will be checked against the processing interface
 *
 * @param <T> the annotation class handled by the processor 
 * @param <I> the interface class handled by the processor
 * 				The scanned classes which matches the declared annotation
 * 				must implements this interface otherwise an exception (runtime)
 * 				will be thrown
 */
public interface IAnnotationProcessor<T extends Annotation, I> {

	/**
	 * Generic parameter position for the annotation class
	 */
	int ANNOTYPE_PARAMETER_POSITION = 0;
	
	/**
	 * Generic parameter position for the interface class
	 */
	int INTERFACE_PARAMETER_POSITION = 1;
	
	
	/**
	 * @param clazz a matching class to process
	 */
	void process(Class<?> clazz);
	
}
