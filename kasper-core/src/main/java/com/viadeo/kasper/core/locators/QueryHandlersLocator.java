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
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.QueryHandlerAdapter;

import java.util.Collection;

/** The Kasper query handlers locator */
public interface QueryHandlersLocator {

    /**
     * @param name the name
     * @param handler the handler to be registered
     * @param domainClass the related domain class
     */
	void registerHandler(String name, QueryHandler handler, Class<? extends Domain> domainClass);

    /**
     * @param name the name of the adapter to be registered
     * @param adapter the adapter instance to be registered
     * @param isGlobal sets TRUE if this adapter must be applied to all handlers
     * @param stickyDomainClass the domain class to be sticky (if global) : filter will only be applied of this domain handlers
     */
    void registerAdapter(String name, QueryHandlerAdapter adapter, boolean isGlobal, Class<? extends Domain> stickyDomainClass);

    /**
     * @param name the name of the adapter to be registered
     * @param adapter the adapter instance to be registered
     * @param isGlobal sets TRUE if this adapter must be applied to all handlers
     */
    void registerAdapter(String name, QueryHandlerAdapter adapter, boolean isGlobal);

    /**
     * @param name the name of the query filter to be registered
     * @param adapter the filter instance to be registered
     */
    void registerAdapter(String name, QueryHandlerAdapter adapter);

    /**
     * @param queryHandlerClass the handler on which the adapter must be applied
     * @param adapterClass the class of the adapter to be applied
     */
    void registerAdapterForQueryHandler(Class<? extends QueryHandler> queryHandlerClass, Class<? extends QueryHandlerAdapter> adapterClass);

	/**
	 * Retrieve an handler instance from its query class
	 *
	 * @param queryClass the query class
	 * @return a corresponding handler instance
	 */
	@SuppressWarnings("rawtypes")
	Optional<QueryHandler<Query,QueryResult>> getHandlerFromQueryClass(Class<? extends Query> queryClass);

	/**
	 * Retrieve a service instance from its query class
	 *
	 * @param queryResultClass the query class
	 * @return a corresponding service instance
	 */
	@SuppressWarnings("rawtypes")
	Collection<QueryHandler> getHandlersFromQueryResultClass(Class<? extends QueryResult> queryResultClass);

	/**
	 * Retrieve an handler instance from its class
	 *
	 * @param handlerClass the query handler class
	 * @return a corresponding handler instance
	 */
	@SuppressWarnings("rawtypes")
	Optional<QueryHandler> getQueryHandlerFromClass(Class<? extends QueryHandler> handlerClass);

	/**
	 * Retrieve a handler instance from its name
	 *
	 * @param handlerName the query handler name
	 * @return a corresponding handler instance
	 */
	@SuppressWarnings("rawtypes")
	Optional<QueryHandler> getHandlerByName(String handlerName);

	/**
	 * Get all registered query handlers
	 *
	 * @return all the registered handlers
	 */
	Collection<QueryHandler> getHandlers();

    /**
     * Get all adapters to be applied on a particular handler class
     *
     * @param handlerClass the class of the service to search adapters for
         * @return a list of adapter instances to apply on the handler
     */
    Collection<QueryHandlerAdapter> getAdaptersForHandlerClass(Class<? extends QueryHandler> handlerClass);


    /**
     * @param adapter the adapter class
     * @return Return true if the adapter is registered, false otherwise
     */
    boolean containsAdapter(Class<? extends QueryHandlerAdapter> adapter);

}
