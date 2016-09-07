// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.locators;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.core.component.command.CommandHandler;
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
