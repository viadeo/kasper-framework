// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.core.boot;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.ddd.Repository;
import com.viadeo.kasper.ddd.annotation.XKasperRepository;
import com.viadeo.kasper.ddd.impl.AbstractRepository;
import org.axonframework.eventhandling.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Process Kasper repositories dynamic registration at kasper platform boot
 *
 * @see XKasperRepository
 */
public class RepositoriesProcessor extends SingletonAnnotationProcessor<XKasperRepository, Repository<?>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RepositoriesProcessor.class);	
	
	/**
	 * The domain locator to register repositories on
	 */
	private transient DomainLocator domainLocator;
	
	/**
	 * The event bus to be injected on domain repositories (Axon dependency for event sourced aggregates)
	 */
	private transient EventBus eventBus;
	
	// ------------------------------------------------------------------------
	
	/**
	 * Process Kasper repository
	 * 
	 * @see com.viadeo.kasper.ddd.Repository
	 * @see AnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	public void process(final Class<?> repositoryClazz, final Repository<?> repository) {
		LOGGER.info("Record on domain locator : " + repositoryClazz.getName());
			
		repository.init();
		
		if (AbstractRepository.class.isAssignableFrom(repositoryClazz)) {
			((AbstractRepository<?>) repository).setEventBus(eventBus);
		}
			
		//- Register the repository to the domain locator ---------------------
		domainLocator.registerRepository(repository);		
	}

	// ------------------------------------------------------------------------
	
	/**
	 * @param domainLocator the domain locator to register repositories on
	 */
	public void setDomainLocator(final DomainLocator domainLocator) {
		this.domainLocator = Preconditions.checkNotNull(domainLocator);
	}
	
	/**
	 * @param eventBus the event bus to be injected in repositories instance
	 */
	public void setEventBus(final EventBus eventBus) {
		this.eventBus = Preconditions.checkNotNull(eventBus);
	}
	
}

