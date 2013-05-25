// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.locators.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.query.IQuery;
import com.viadeo.kasper.cqrs.query.IQueryService;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryRuntimeException;
import com.viadeo.kasper.locators.IQueryServicesLocator;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

/** Base implementation for query services locator */
public class QueryServicesLocatorBase implements IQueryServicesLocator {

	/** Registered services */
	@SuppressWarnings("rawtypes")
	private final Map<Class<? extends IQueryService>, IQueryService> services = newHashMap();

	/** Registered query classes and associated service instances */
	private final Map<Class<? extends IQuery>, IQueryService<?,?>> serviceQueryClasses = newHashMap();

	/** Registered services names and associated service instances */
	@SuppressWarnings("rawtypes")
	private final Map<String, IQueryService> serviceNames = newHashMap();

	// ------------------------------------------------------------------------

	@SuppressWarnings("rawtypes")
	@Override
	public void registerService(final String name, final IQueryService service) {
		checkNotNull(name);
		checkNotNull(service);

		if (name.isEmpty()) {
			throw new KasperQueryRuntimeException("Name of services cannot be empty : " + service.getClass());
		}

		final Class<? extends IQueryService> serviceClass = service.getClass();

		@SuppressWarnings("unchecked") // Safe
		final Optional<Class<? extends IQuery>> optQueryClass =
				(Optional<Class<? extends IQuery>>)
						ReflectionGenericsResolver.getParameterTypeFromClass(
								service.getClass(), IQueryService.class, IQueryService.PARAMETER_QUERY_POSITION);

		if (!optQueryClass.isPresent()) {
			throw new KasperQueryRuntimeException("Unable to find query class for service " + service.getClass());
		}

		final Class<? extends IQuery> queryClass = optQueryClass.get();
		if (this.serviceQueryClasses.containsKey(queryClass)) {
			throw new KasperQueryRuntimeException("A service for the same query class is already registered : " + queryClass);
		}

		if (this.serviceNames.containsKey(name)) {
			throw new KasperQueryRuntimeException("A service by the same name is already registered : " + name);
		}

		this.serviceQueryClasses.put(queryClass, service);
		this.serviceNames.put(name, service);
		this.services.put(serviceClass, service);
	}

	// ------------------------------------------------------------------------

	@SuppressWarnings("rawtypes")
	@Override
	public Optional<IQueryService> getServiceFromClass(
			final Class<? extends IQueryService<?, ?>> serviceClass) {
		final IQueryService service = this.services.get(serviceClass);
		return Optional.fromNullable(service);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Optional<IQueryService> getServiceByName(final String serviceName) {
		final IQueryService service = this.serviceNames.get(serviceName);
		return Optional.fromNullable(service);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Optional<IQueryService> getServiceFromQueryClass(Class<? extends IQuery> queryClass) {
		final IQueryService service = this.serviceQueryClasses.get(queryClass);
		return Optional.fromNullable(service);
	}

	@Override
	public Collection<IQueryService<?, ?>> getServices() {
		return Collections.unmodifiableCollection(this.serviceQueryClasses.values());
	}

}
