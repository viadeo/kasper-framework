// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.locators;

import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryHandlerAdapter;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.ddd.Domain;

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
