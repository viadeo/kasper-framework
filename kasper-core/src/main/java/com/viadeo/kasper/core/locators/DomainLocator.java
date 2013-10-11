// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.locators;

import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.Entity;
import com.viadeo.kasper.ddd.IRepository;

import java.util.Collection;
import java.util.Set;

/**
 *
 * The domain locator interface
 * - record domains, entities, repositories and services
 * 
 * TODO: terminate javadoc
 * 
 */
public interface DomainLocator {

    /**
     * Register a new commandHandler
     */
    void registerHandler(CommandHandler commandHandler);

    /**
     * Get all registered command handlers
     */
    Collection<CommandHandler> getHandlers();

    /**
     * @return an optional handler for the specified command class
     */
    Optional<CommandHandler> getHandlerForCommandClass(Class<? extends Command> commandClass);

	/**
	 * Register a new domain to the locator
	 * 
	 * @param domain the domain to register
	 * @param name the name of the domain
	 * @param prefix the prefix assigned to this domain
	 */
	void registerDomain(Domain domain, String name, String prefix);

	/**
	 * Return the prefix of a specified domain
	 * 
	 * @param domain the domain
	 * @return the domain prefix
	 */
	String getDomainPrefix(Domain domain);

	/**
	 * Return the name of a specified domain
	 *
	 * @param domain the domain
	 * @return the domain name
	 */
	String getDomainName(Domain domain);

	/**
	 * Search for a domain by name
	 * 
	 * @param name the name of the domain to be searched
	 * @return the (optional) domain
	 */
	<D extends Domain> Optional<D> getDomainByName(String name);

	/**
	 * Search for a domain by prefix
	 * 
	 * @param prefix the prefix of the domain to be searched
	 * @return the (optional) domain
	 */
	<D extends Domain> Optional<D> getDomainByPrefix(String prefix);

	/**
	 * @return all registered domains
	 */
	Set<Domain> getDomains();

	/**
	 * Return the domain related to the specified entity
	 * 
	 * @param entity the entitiy to search for domain
	 * @return the domain of the entity
	 */
	<D extends Domain> Optional<D> getEntityDomain(Entity entity);

	/**
	 * Return a set with all the entities of the specified domain
	 * 
	 * @param domain the domain
	 * @return a set with all entities related to this domain
	 */
	<D extends Domain> Set<? extends Entity> getDomainEntities(D domain);

	/**
	 * Return a set with all the entities of the specified domain class
	 * 
	 * @param domain the domain class
	 * @return a set with all entities related to this domain class
	 */
	<D extends Domain> Set<? extends Entity> getDomainEntities(Class<D> domain);

	/**
	 * Register a new domain repository
	 * 
	 * @param repository the repository to register
	 */
	void registerRepository(IRepository repository);

	/**
	 * Get the repository for an entity
	 * 
	 * @param entity the entity
	 * @return the repository responsible for storing this entity
	 */
	<E extends AggregateRoot> Optional<IRepository<E>> getEntityRepository(E entity);

	/**
	 * Get the repository for an entity class
	 * 
	 * @param entityClass the entity class
	 * @return the repository responsible for storing this class of entities
	 */
	<E extends AggregateRoot> Optional<IRepository<E>> getEntityRepository(Class<E> entityClass);

}
