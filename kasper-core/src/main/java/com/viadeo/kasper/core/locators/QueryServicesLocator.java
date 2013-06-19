// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.core.locators;

import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryService;

import java.util.Collection;

/** The Kasper query services locator */
public interface QueryServicesLocator {

	/** @param service the service to be registered */
	void registerService(String name, QueryService<?, ?> service);

	/**
	 * Retrieve a service instance from its query class
	 *
	 * @param queryClass the query class
	 * @return a corresponding service instance
	 */
	@SuppressWarnings("rawtypes")
	Optional<QueryService> getServiceFromQueryClass(Class<? extends Query> queryClass);

	/**
	 * Retrieve a service instance from its class
	 *
	 * @param serviceClass the query service class
	 * @return a corresponding service instance
	 */
	@SuppressWarnings("rawtypes")
	Optional<QueryService> getServiceFromClass(Class<? extends QueryService<?, ?>> serviceClass);

	/**
	 * Retrieve a service instance from its name
	 *
	 * @param serviceName the query service name
	 * @return a corresponding service instance
	 */
	@SuppressWarnings("rawtypes")
	Optional<QueryService> getServiceByName(String serviceName);

	/**
	 * Get all registered query services
	 *
	 * @return all the registered services
	 */
	Collection<QueryService<?,?>> getServices();

}
