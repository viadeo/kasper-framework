// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.core.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.ddd.IInternalDomain;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;
import com.viadeo.kasper.locators.IDomainLocator;

/**
 *
 * Process Kasper domain services dynamic registration at platform boot
 *
 * @see XKasperDomain
 */
public class DomainsProcessor extends AbstractSingletonAnnotationProcessor<XKasperDomain, IInternalDomain> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DomainsProcessor.class);

	/**
	 * The domain locator to register domains on
	 */
	private transient IDomainLocator domainLocator;

	// ------------------------------------------------------------------------

	/**
	 * Process Kasper domain
	 * 
	 * @see IDomain
	 * @see com.viadeo.kasper.core.boot.IAnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	public void process(final Class<?> domainClazz, final IInternalDomain domain) {
		LOGGER.info("Record on domain locator : " + domainClazz.getName());

		//- Register the domain to the locator --------------------------------
		final XKasperDomain annotation = domainClazz.getAnnotation(XKasperDomain.class);
		this.domainLocator.registerDomain(domain, annotation.label(), annotation.prefix());
	}

	// ------------------------------------------------------------------------

	/**
	 * @param domainLocator the domain locator for registering domains
	 */
	public void setDomainLocator(final IDomainLocator domainLocator) {
		this.domainLocator = Preconditions.checkNotNull(domainLocator);
	}
	
}

