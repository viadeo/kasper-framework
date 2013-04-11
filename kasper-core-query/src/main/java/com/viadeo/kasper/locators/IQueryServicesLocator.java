// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.locators;

import java.util.Collection;

import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.query.IQuery;
import com.viadeo.kasper.cqrs.query.IQueryService;

/** The Kasper query services locator */
public interface IQueryServicesLocator {

	/** @param service the service to be registered */
	void registerService(String name, IQueryService<?, ?> service);

	/**
	 * Retrieve a service instance from its query class
	 *
	 * @param queryClass the query class
	 * @return a corresponding service instance
	 */
	Optional<IQueryService> getServiceFromQueryClass(Class<? extends IQuery> queryClass);

	/**
	 * Retrieve a service instance from its class
	 *
	 * @param serviceClass the query service class
	 * @return a corresponding service instance
	 */
	@SuppressWarnings("rawtypes")
	Optional<IQueryService> getServiceFromClass(Class<? extends IQueryService<?, ?>> serviceClass);

	/**
	 * Retrieve a service instance from its name
	 *
	 * @param serviceName the query service name
	 * @return a corresponding service instance
	 */
	@SuppressWarnings("rawtypes")
	Optional<IQueryService> getServiceByName(String serviceName);

	/**
	 * Get all registered query services
	 *
	 * @return all the registered services
	 */
	@SuppressWarnings("rawtypes")
	Collection<IQueryService> getServices();

}
