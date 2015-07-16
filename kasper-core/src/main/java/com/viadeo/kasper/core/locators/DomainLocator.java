// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.locators;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.core.component.command.aggregate.ddd.Entity;

import java.util.Collection;
import java.util.Set;

/**
 *
 * The domain locator interface
 * - record domains, entities, repositories and handlers
 * 
 * TODO: terminate javadoc
 * 
 */
public interface DomainLocator {

    /**
     * Register a new commandHandler
     * @param commandHandler a command handler
     */
    void registerHandler(CommandHandler commandHandler);

    /**
     * Get all registered command handlers
     * @return all registered command handlers
     */
    Collection<CommandHandler> getHandlers();

    /**
     * @param commandClass a class of a <code>Command</code>
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
     * @param <D> the domain
	 * @return the (optional) domain
	 */
	<D extends Domain> Optional<D> getDomainByName(String name);

	/**
	 * Search for a domain by prefix
	 * 
	 * @param prefix the prefix of the domain to be searched
     * @param <D> the domain
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
	 * @param entity the entity to search for domain
     * @param <D> the domain
	 * @return the domain of the entity
	 */
	<D extends Domain> Optional<D> getEntityDomain(Entity entity);

	/**
	 * Return a set with all the entities of the specified domain
	 * 
	 * @param domain the domain
     * @param <D> the domain
	 * @return a set with all entities related to this domain
	 */
	<D extends Domain> Set<? extends Entity> getDomainEntities(D domain);

	/**
	 * Return a set with all the entities of the specified domain class
	 * 
	 * @param domain the domain class
     * @param <D> the domain
	 * @return a set with all entities related to this domain class
	 */
	<D extends Domain> Set<? extends Entity> getDomainEntities(Class<D> domain);

}
