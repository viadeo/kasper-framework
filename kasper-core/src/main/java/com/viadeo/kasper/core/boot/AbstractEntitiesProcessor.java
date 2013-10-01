// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.core.boot;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.ddd.Entity;
import org.axonframework.eventhandling.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;

/**
 * 
 * Process Kasper repositories dynamic registration at kasper platform boot
 *
 * @see com.viadeo.kasper.ddd.annotation.XKasperRepository
 */
public abstract class AbstractEntitiesProcessor<T extends Annotation, I extends Entity> implements AnnotationProcessor<T, I> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEntitiesProcessor.class);

	/**
	 * The event bus to be injected on domain repositories (Axon dependency for event sourced aggregates)
	 */
	private transient EventBus eventBus;

	// ------------------------------------------------------------------------

    @Override
    public boolean isAnnotationMandatory() {
        return true;
    }

    // -------------------------------------------------------------------------

	/**
	 * Process Kasper repository
	 *
	 * @see com.viadeo.kasper.ddd.IRepository
	 * @see AnnotationProcessor#process(Class)
	 */
	@Override
	public void process(final Class<?> entityClass) {
        // Currently does nothing
	}

	// ------------------------------------------------------------------------
	
	/**
	 * @param eventBus the event bus to be injected in repositories instance
	 */
	public void setEventBus(final EventBus eventBus) {
		this.eventBus = Preconditions.checkNotNull(eventBus);
	}

}

