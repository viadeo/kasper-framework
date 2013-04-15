// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.locators.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.IDomain;
import com.viadeo.kasper.ddd.IAggregateRoot;
import com.viadeo.kasper.ddd.IEntity;
import com.viadeo.kasper.ddd.IInternalDomain;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.exception.KasperDomainRuntimeException;
import com.viadeo.kasper.ddd.impl.AbstractDomain;
import com.viadeo.kasper.locators.IDomainLocator;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

/**
 *
 * Base implementation for domain locator
 *
 */
public class DomainLocatorBase implements IDomainLocator {

	//- Convenient Cache types ------------------------------------------------

	private static final class RepositoriesByAggregateCache extends HashMap<Class<? extends IAggregateRoot>, IRepository<?>> {
		private static final long serialVersionUID = 4713909577649004213L;
	}

	private static final class DomainsPropertiesCache extends HashMap<IDomain, Map<String, String>> {
		private static final long serialVersionUID = -145508661436546886L;
	}

	private static final class DomainByPropertyCache extends HashMap<String, IDomain> {
		private static final long serialVersionUID = 4967890441255351599L;
	}

	// ------------------------------------------------------------------------

	/** Domain repositories  */
	private transient final RepositoriesByAggregateCache entityRepositories;

	/** Domains */
	private transient final DomainsPropertiesCache domains;
	private transient final DomainByPropertyCache domainNames;
	private transient final DomainByPropertyCache domainPrefixes;

	// ------------------------------------------------------------------------

	public DomainLocatorBase() {
		this.entityRepositories = new RepositoriesByAggregateCache();
		this.domains = new DomainsPropertiesCache();
		this.domainNames = new DomainByPropertyCache();
		this.domainPrefixes = new DomainByPropertyCache();
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.locators.IDomainLocator#getDomainEntities(com.viadeo.kasper.IDomain)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <D extends IDomain> Set<? extends IEntity> getDomainEntities(final D _domain) {
		Preconditions.checkNotNull(_domain);
		// TODO Auto-generated method stub

		return Collections.EMPTY_SET;
	}

	/**
	 * @see com.viadeo.kasper.locators.IDomainLocator#getDomainEntities(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <D extends IDomain> Set<? extends IEntity> getDomainEntities(final Class<D> _domain) {
		Preconditions.checkNotNull(_domain);
		// TODO Auto-generated method stub

		return Collections.EMPTY_SET;
	}

	/**
	 * @see com.viadeo.kasper.locators.IDomainLocator#getEntityDomain(com.viadeo.kasper.ddd.IEntity)
	 */
	@Override
	public <D extends IDomain> D getEntityDomain(final IEntity _entity) {
		Preconditions.checkNotNull(_entity);
		// TODO Auto-generated method stub
		throw new KasperDomainRuntimeException("Entity has no registered domain : " + _entity.getClass().getName());
	}

	// ========================================================================

	/**
	 * @see com.viadeo.kasper.locators.IDomainLocator#registerRepository(com.viadeo.kasper.ddd.IRepository)
	 */
	@Override
	public void registerRepository(final IRepository<?> repository) {
		Preconditions.checkNotNull(repository);

		@SuppressWarnings("unchecked") // Safe
		final Optional<Class<? extends IAggregateRoot>> entity = 
				(Optional<Class<? extends IAggregateRoot>>) 
					ReflectionGenericsResolver.getParameterTypeFromClass(repository.getClass(),
							IRepository.class, IRepository.ENTITY_PARAMETER_POSITION);

		if (!entity.isPresent()) {
			throw new KasperDomainRuntimeException("Entity type cannot be determined for " + repository.getClass().getName());
		}

		this.entityRepositories.put(entity.get(), repository);
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.locators.IDomainLocator#getEntityRepository(com.viadeo.kasper.ddd.IAggregateRoot)
	 */
	@SuppressWarnings("unchecked") // Safe
	@Override
	public <E extends IAggregateRoot> IRepository<E> getEntityRepository(final E _entity) {
		Preconditions.checkNotNull(_entity);
		return (IRepository<E>) this.entityRepositories.get(_entity.getClass());
	}


	/**
	 * @see com.viadeo.kasper.locators.IDomainLocator#getEntityRepository(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <E extends IAggregateRoot> IRepository<E> getEntityRepository(final Class<E> _entityClass) {
		Preconditions.checkNotNull(_entityClass);

		return (IRepository<E>) this.entityRepositories.get(_entityClass);
	}

	// ========================================================================

	/**
	 * @see com.viadeo.kasper.locators.IDomainLocator#registerDomain(com.viadeo.kasper.IDomain, java.lang.String, java.lang.String)
	 */
	@Override
	public void registerDomain(final IInternalDomain domain, final String name, final String prefix) {
		Preconditions.checkNotNull(domain);
		Preconditions.checkNotNull(name);
		Preconditions.checkNotNull(prefix);

		if (name.isEmpty() || prefix.isEmpty()) {
			throw new KasperDomainRuntimeException("Domain name and prefix must not be empty for domain" + domain.getClass().getSimpleName());
		}

		if (AbstractDomain.class.isAssignableFrom(domain.getClass())) {
			((AbstractDomain) domain).setDomainLocator(this); // Create locator link
		}

		final Map<String, String> domainData = new HashMap<String, String>();
		domainData.put("prefix", prefix); // FIXME: string as static class prop
		domainData.put("name", name); // FIXME: string as static class prop
		this.domains.put(domain, domainData);

		this.domainNames.put(name, domain);
		this.domainPrefixes.put(prefix, domain);
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.locators.IDomainLocator#getDomainByName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <D extends IDomain> Optional<D> getDomainByName(final String name) {
		Preconditions.checkNotNull(name);
		return Optional.fromNullable((D) this.domainNames.get(name));
	}

	/**
	 * @see com.viadeo.kasper.locators.IDomainLocator#getDomainByPrefix(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <D extends IDomain> Optional<D> getDomainByPrefix(final String prefix) {
		Preconditions.checkNotNull(prefix);
		return Optional.fromNullable((D) this.domainPrefixes.get(prefix));
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.locators.IDomainLocator#getDomainPrefix(com.viadeo.kasper.IDomain)
	 */
	@Override
	public String getDomainPrefix(final IDomain _domain) {
		Preconditions.checkNotNull(_domain);
		if (this.domains.containsKey(_domain.getClass())) {
			return this.domains.get(_domain.getClass()).get("prefix");
		}
		throw new KasperDomainRuntimeException("Domain has not been recorded : " + _domain.getClass().getName());
	}

	/**
	 * @see com.viadeo.kasper.locators.IDomainLocator#getDomainName(com.viadeo.kasper.IDomain)
	 */
	@Override
	public String getDomainName(final IDomain _domain) {
		Preconditions.checkNotNull(_domain);
		if (this.domains.containsKey(_domain.getClass())) {
			return this.domains.get(_domain.getClass()).get("name");
		}
		throw new KasperDomainRuntimeException("Domain has not been recorded : " + _domain.getClass().getName());
	}

	/**
	 * @see com.viadeo.kasper.locators.IDomainLocator#getDomains()
	 */
	@Override
	public Set<IDomain> getDomains() {
		return Collections.unmodifiableSet(this.domains.keySet()); 
	}

}
