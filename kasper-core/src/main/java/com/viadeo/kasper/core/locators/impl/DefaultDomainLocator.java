// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.locators.impl;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.Entity;
import com.viadeo.kasper.ddd.Repository;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.util.*;

/**
 * Base implementation for domain locator
 */
public class DefaultDomainLocator implements DomainLocator {

    // - Convenient Cache types ------------------------------------------------

    private static final class RepositoriesByAggregateCache extends
            HashMap<Class<? extends AggregateRoot>, Repository<?>> {
        private static final long serialVersionUID = 4713909577649004213L;
    }

    private static final class DomainsPropertiesCache extends HashMap<Domain, Map<String, String>> {
        private static final long serialVersionUID = -145508661436546886L;
    }

    private static final class DomainByPropertyCache extends HashMap<String, Domain> {
        private static final long serialVersionUID = 4967890441255351599L;
    }

    private final List<CommandHandler<? extends Command>> handlers = new ArrayList<>();

    // ------------------------------------------------------------------------

    /** Domain repositories */
    private final transient RepositoriesByAggregateCache entityRepositories;

    /** Domains */
    private final transient DomainsPropertiesCache domains;
    private final transient DomainByPropertyCache domainNames;
    private final transient DomainByPropertyCache domainPrefixes;

    // ------------------------------------------------------------------------

    public DefaultDomainLocator() {
        this.entityRepositories = new RepositoriesByAggregateCache();
        this.domains = new DomainsPropertiesCache();
        this.domainNames = new DomainByPropertyCache();
        this.domainPrefixes = new DomainByPropertyCache();
    }

    // ------------------------------------------------------------------------

    @Override
    public void registerHandler(CommandHandler<? extends Command> commandHandler) {
        handlers.add(commandHandler);
    }

    @Override
    public Collection<CommandHandler<? extends Command>> getHandlers() {
        return Collections.unmodifiableCollection(handlers);
    }

    /**
     * @see com.viadeo.kasper.core.locators.DomainLocator#getDomainEntities(com.viadeo.kasper.ddd.Domain)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <D extends Domain> Set<? extends Entity> getDomainEntities(final D domain) {
        Preconditions.checkNotNull(domain);
        // TODO Auto-generated method stub

        return Collections.EMPTY_SET;
    }

    /**
     * @see com.viadeo.kasper.core.locators.DomainLocator#getDomainEntities(java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <D extends Domain> Set<? extends Entity> getDomainEntities(final Class<D> domain) {
        Preconditions.checkNotNull(domain);
        // TODO Auto-generated method stub

        return Collections.EMPTY_SET;
    }

    /**
     * @see com.viadeo.kasper.core.locators.DomainLocator#getEntityDomain(com.viadeo.kasper.ddd.Entity)
     */
    @Override
    public <D extends Domain> D getEntityDomain(final Entity entity) {
        Preconditions.checkNotNull(entity);
        // TODO Auto-generated method stub
        throw new KasperException("Entity has no registered domain : " + entity.getClass().getName());
    }

    // ========================================================================

    /**
     * @see com.viadeo.kasper.core.locators.DomainLocator#registerRepository(com.viadeo.kasper.ddd.Repository)
     */
    @Override
    public void registerRepository(final Repository<?> repository) {
        Preconditions.checkNotNull(repository);

        @SuppressWarnings("unchecked")
        // Safe
        final Optional<Class<? extends AggregateRoot>> entity = (Optional<Class<? extends AggregateRoot>>) ReflectionGenericsResolver
                .getParameterTypeFromClass(repository.getClass(), Repository.class,
                        Repository.ENTITY_PARAMETER_POSITION);

        if (!entity.isPresent()) {
            throw new KasperException("Entity type cannot be determined for " + repository.getClass().getName());
        }

        this.entityRepositories.put(entity.get(), repository);
    }

    // ------------------------------------------------------------------------

    /**
     * @see com.viadeo.kasper.core.locators.DomainLocator#getEntityRepository(com.viadeo.kasper.ddd.AggregateRoot)
     */
    @SuppressWarnings("unchecked")
    // Safe
    @Override
    public <E extends AggregateRoot> Repository<E> getEntityRepository(final E entity) {
        Preconditions.checkNotNull(entity);
        return (Repository<E>) this.entityRepositories.get(entity.getClass());
    }

    /**
     * @see com.viadeo.kasper.core.locators.DomainLocator#getEntityRepository(java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <E extends AggregateRoot> Repository<E> getEntityRepository(final Class<E> entityClass) {
        Preconditions.checkNotNull(entityClass);

        return (Repository<E>) this.entityRepositories.get(entityClass);
    }

    // ========================================================================

    @Override
    public void registerDomain(final Domain domain, final String name, final String prefix) {
        Preconditions.checkNotNull(domain);
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(prefix);

        if (name.isEmpty() || prefix.isEmpty()) {
            throw new KasperException("Domain name and prefix must not be empty for domain"
                    + domain.getClass().getSimpleName());
        }

        final Map<String, String> domainData = new HashMap<>();
        domainData.put("prefix", prefix); // FIXME: string as static class prop
        domainData.put("name", name); // FIXME: string as static class prop
        this.domains.put(domain, domainData);

        this.domainNames.put(name, domain);
        this.domainPrefixes.put(prefix, domain);
    }

    // ------------------------------------------------------------------------

    /**
     * @see com.viadeo.kasper.core.locators.DomainLocator#getDomainByName(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <D extends Domain> Optional<D> getDomainByName(final String name) {
        Preconditions.checkNotNull(name);
        return Optional.fromNullable((D) this.domainNames.get(name));
    }

    /**
     * @see com.viadeo.kasper.core.locators.DomainLocator#getDomainByPrefix(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <D extends Domain> Optional<D> getDomainByPrefix(final String prefix) {
        Preconditions.checkNotNull(prefix);
        return Optional.fromNullable((D) this.domainPrefixes.get(prefix));
    }

    // ------------------------------------------------------------------------

    /**
     * @see com.viadeo.kasper.core.locators.DomainLocator#getDomainPrefix(com.viadeo.kasper.ddd.Domain)
     */
    @Override
    public String getDomainPrefix(final Domain domain) {
        Preconditions.checkNotNull(domain);
        if (this.domains.containsKey(domain.getClass())) {
            return this.domains.get(domain.getClass()).get("prefix");
        }
        throw new KasperException("Domain has not been recorded : " + domain.getClass().getName());
    }

    /**
     * @see com.viadeo.kasper.core.locators.DomainLocator#getDomainName(com.viadeo.kasper.ddd.Domain)
     */
    @Override
    public String getDomainName(final Domain domain) {
        Preconditions.checkNotNull(domain);
        if (this.domains.containsKey(domain.getClass())) {
            return this.domains.get(domain.getClass()).get("name");
        }
        throw new KasperException("Domain has not been recorded : " + domain.getClass().getName());
    }

    /**
     * @see com.viadeo.kasper.core.locators.DomainLocator#getDomains()
     */
    @Override
    public Set<Domain> getDomains() {
        return Collections.unmodifiableSet(this.domains.keySet());
    }

}
