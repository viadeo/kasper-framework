// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.core.locators.impl;

import com.google.common.base.Optional;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryService;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

/** Base implementation for query services locator */
public class DefaultQueryServicesLocator implements QueryServicesLocator {

	/** Registered services */
	@SuppressWarnings("rawtypes")
    private final ClassToInstanceMap<QueryService> services = MutableClassToInstanceMap.create();

	/** Registered query classes and associated service instances */
	private final Map<Class<? extends Query>, QueryService<?,?>> serviceQueryClasses = newHashMap();

	/** Registered services names and associated service instances */
	@SuppressWarnings("rawtypes")
	private final Map<String, QueryService> serviceNames = newHashMap();

	// ------------------------------------------------------------------------

	@SuppressWarnings("rawtypes")
	@Override
	public void registerService(final String name, final QueryService service) {
		checkNotNull(name);
		checkNotNull(service);

		if (name.isEmpty()) {
			throw new KasperQueryException("Name of services cannot be empty : " + service.getClass());
		}

		final Class<? extends QueryService> serviceClass = service.getClass();

		@SuppressWarnings("unchecked") // Safe
		final Optional<Class<? extends Query>> optQueryClass =
				(Optional<Class<? extends Query>>)
						ReflectionGenericsResolver.getParameterTypeFromClass(
								service.getClass(), QueryService.class, QueryService.PARAMETER_QUERY_POSITION);

		if (!optQueryClass.isPresent()) {
			throw new KasperQueryException("Unable to find query class for service " + service.getClass());
		}

		final Class<? extends Query> queryClass = optQueryClass.get();
		if (this.serviceQueryClasses.containsKey(queryClass)) {
			throw new KasperQueryException("A service for the same query class is already registered : " + queryClass);
		}

		if (this.serviceNames.containsKey(name)) {
			throw new KasperQueryException("A service by the same name is already registered : " + name);
		}

		this.serviceQueryClasses.put(queryClass, service);
		this.serviceNames.put(name, service);
		this.services.put(serviceClass, service);
	}

	// ------------------------------------------------------------------------

	@SuppressWarnings("rawtypes")
	@Override
	public Optional<QueryService> getServiceFromClass(
			final Class<? extends QueryService<?, ?>> serviceClass) {
		final QueryService service = this.services.getInstance(serviceClass);
		return Optional.fromNullable(service);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Optional<QueryService> getServiceByName(final String serviceName) {
		final QueryService service = this.serviceNames.get(serviceName);
		return Optional.fromNullable(service);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Optional<QueryService> getServiceFromQueryClass(Class<? extends Query> queryClass) {
		final QueryService service = this.serviceQueryClasses.get(queryClass);
		return Optional.fromNullable(service);
	}

	@Override
	public Collection<QueryService<?, ?>> getServices() {
		return Collections.unmodifiableCollection(this.serviceQueryClasses.values());
	}

}
