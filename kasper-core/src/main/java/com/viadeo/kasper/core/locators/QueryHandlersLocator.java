// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.locators;

import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.RequestActorsChain;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.ddd.Domain;

import java.util.Collection;

/** The Kasper query handlers locator */
public interface QueryHandlersLocator {

	/** @param handler the handler to be registered */
	void registerHandler(String name, QueryHandler handler, Class<? extends Domain> domainClass);

    /**
     * @param filterName the name of the query filter to be registered
     * @param queryFilter the filter instance to be registered
     * @param isGlobal sets TRUE if this filter must be applied to all handlers
     * @param stickyDomainClass the domain class to be sticky (if global) : filter will only be applied of this domain handlers
     */
    void registerFilter(String filterName, QueryHandlerFilter queryFilter, boolean isGlobal, Class<? extends Domain> stickyDomainClass);

    /**
     * @param filterName the name of the query filter to be registered
     * @param queryFilter the filter instance to be registered
     * @param isGlobal sets TRUE if this filter must be applied to all handlers
     */
    void registerFilter(String filterName, QueryHandlerFilter queryFilter, boolean isGlobal);

    /**
     * @param filterName the name of the query filter to be registered
     * @param queryFilter the filter instance to be registered
     */
    void registerFilter(String filterName, QueryHandlerFilter queryFilter);

    /**
     * @param queryHandlerClass the handler on which the filter must be applied
     * @param filterClass the class of the filter to be applied
     */
    void registerFilterForQueryHandler(Class<? extends QueryHandler> queryHandlerClass, Class<? extends QueryHandlerFilter> filterClass);

	/**
	 * Retrieve an handler instance from its query class
	 *
	 * @param queryClass the query class
	 * @return a corresponding handler instance
	 */
	@SuppressWarnings("rawtypes")
	Optional<QueryHandler> getHandlerFromQueryClass(Class<? extends Query> queryClass);

	/**
	 * Retrieve a service instance from its query class
	 *
	 * @param queryClass the query class
	 * @return a corresponding service instance
	 */
	@SuppressWarnings("rawtypes")
	Collection<QueryService> getServicesFromQueryAnswerClass(Class<? extends QueryAnswer> queryClass);

    /*
     * Retrieve the actors chain for a specified query class
     *
     * @param queryClass the query class
     * @return the computed actors chain
     */
    <Q extends Query, P extends QueryResult, R extends QueryResponse<P>>
    Optional<RequestActorsChain<Q, R>> getRequestActorChain(Class<? extends Q> queryClass);

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
     * Get all filters to be applied on a particular handler class
     *
     * @param handlerClass the class of the servcie to search filters for
     * @return a list of filter instances to apply on the handler
     */
    Collection<QueryHandlerFilter> getFiltersForHandlerClass(Class<? extends QueryHandler> handlerClass);

}
