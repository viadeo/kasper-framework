// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.locators.impl;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.resolvers.CommandHandlerResolver;
import com.viadeo.kasper.core.resolvers.RepositoryResolver;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.Entity;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.repository.ClientRepository;
import com.viadeo.kasper.exception.KasperException;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base implementation for domain locator
 */
public class DefaultDomainLocator implements DomainLocator {

    // - Convenient Cache types ------------------------------------------------

    private static final class RepositoriesByAggregateCache extends
            HashMap<Class<? extends AggregateRoot>, IRepository> {
        private static final long serialVersionUID = 4713909577649004213L;
    }

    private static final class DomainsPropertiesCache extends HashMap<Domain, Map<String, String>> {
        private static final long serialVersionUID = -145508661436546886L;
    }

    private static final class DomainByPropertyCache extends HashMap<String, Domain> {
        private static final long serialVersionUID = 4967890441255351599L;
    }

    private final Map<CommandHandler, Class<? extends Command>> handlers = Maps.newHashMap();

    // ------------------------------------------------------------------------

    /** Domain repositories */
    private final transient RepositoriesByAggregateCache entityRepositories;

    /** Domains */
    private final transient DomainsPropertiesCache domains;
    private final transient DomainByPropertyCache domainNames;
    private final transient DomainByPropertyCache domainPrefixes;

    // ------------------------------------------------------------------------

    private RepositoryResolver repositoryResolver;
    private CommandHandlerResolver commandHandlerResolver;

    // ------------------------------------------------------------------------

    public DefaultDomainLocator() {
        this.entityRepositories = new RepositoriesByAggregateCache();
        this.domains = new DomainsPropertiesCache();
        this.domainNames = new DomainByPropertyCache();
        this.domainPrefixes = new DomainByPropertyCache();
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void registerHandler(final CommandHandler commandHandler) {
        final Class<? extends Command> commandClass =
                commandHandlerResolver.getCommandClass(commandHandler.getClass());
        handlers.put(commandHandler, commandClass);
    }

    @Override
    public Collection<CommandHandler> getHandlers() {
        return Collections.unmodifiableCollection(handlers.keySet());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<CommandHandler> getHandlerForCommandClass(Class<? extends Command> commandClass) {
        for (final CommandHandler commandHandler : handlers.keySet()) {
            final Class<? extends Command> command = handlers.get(commandHandler);
            if (command.equals(commandClass)) {
                return Optional.of(commandHandler);
            }
        }
        return Optional.absent();
    }

    /**
     * @see com.viadeo.kasper.core.locators.DomainLocator#getDomainEntities(com.viadeo.kasper.ddd.Domain)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <D extends Domain> Set<? extends Entity> getDomainEntities(final D domain) {
        checkNotNull(domain);
        // TODO Auto-generated method stub

        return Collections.EMPTY_SET;
    }

    /**
     * @see com.viadeo.kasper.core.locators.DomainLocator#getDomainEntities(java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <D extends Domain> Set<? extends Entity> getDomainEntities(final Class<D> domain) {
        checkNotNull(domain);
        // TODO Auto-generated method stub

        return Collections.EMPTY_SET;
    }

    /**
     * @see com.viadeo.kasper.core.locators.DomainLocator#getEntityDomain(com.viadeo.kasper.ddd.Entity)
     */
    @Override
    public <D extends Domain> Optional<D> getEntityDomain(final Entity entity) {
        checkNotNull(entity);
        // TODO Auto-generated method stub
        throw new KasperException("Entity has no registered domain : " + entity.getClass().getName());
    }

    // ========================================================================

    /**
     * @see com.viadeo.kasper.core.locators.DomainLocator#registerRepository(com.viadeo.kasper.ddd.IRepository)
     */
    @Override
    public void registerRepository(final IRepository repository) {
        checkNotNull(repository);

        final Class<? extends AggregateRoot> entity =
                repositoryResolver.getStoredEntityClass(repository.getClass());

        this.entityRepositories.put(entity, repository);
    }

    // ------------------------------------------------------------------------

    /**
     * @see com.viadeo.kasper.core.locators.DomainLocator#getEntityRepository(com.viadeo.kasper.ddd.AggregateRoot)
     */
    @SuppressWarnings("unchecked")
    // Safe
    @Override
    public <E extends AggregateRoot> Optional<ClientRepository<E>> getEntityRepository(final E entity) {
        checkNotNull(entity);
        return Optional.fromNullable(new ClientRepository<E>((IRepository<E>) this.entityRepositories.get(entity.getClass())));
    }

    /**
     * @see com.viadeo.kasper.core.locators.DomainLocator#getEntityRepository(java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <E extends AggregateRoot> Optional<ClientRepository<E>> getEntityRepository(final Class<E> entityClass) {
        checkNotNull(entityClass);

        return Optional.fromNullable(new ClientRepository<E>((IRepository<E>) this.entityRepositories.get(entityClass)));
    }

    // ========================================================================

    @Override
    public void registerDomain(final Domain domain, final String name, final String prefix) {
        checkNotNull(domain);
        checkNotNull(name);
        checkNotNull(prefix);

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
        checkNotNull(name);
        return Optional.fromNullable((D) this.domainNames.get(name));
    }

    /**
     * @see com.viadeo.kasper.core.locators.DomainLocator#getDomainByPrefix(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <D extends Domain> Optional<D> getDomainByPrefix(final String prefix) {
        checkNotNull(prefix);
        return Optional.fromNullable((D) this.domainPrefixes.get(prefix));
    }

    // ------------------------------------------------------------------------

    /**
     * @see com.viadeo.kasper.core.locators.DomainLocator#getDomainPrefix(com.viadeo.kasper.ddd.Domain)
     */
    @Override
    public String getDomainPrefix(final Domain domain) {
        checkNotNull(domain);

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
        checkNotNull(domain);

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

    // ------------------------------------------------------------------------

    public void setRepositoryResolver(final RepositoryResolver repositoryResolver) {
        this.repositoryResolver = checkNotNull(repositoryResolver);
    }

    public void setCommandHandlerResolver(final CommandHandlerResolver commandHandlerResolver) {
        this.commandHandlerResolver = checkNotNull(commandHandlerResolver);
    }

}
