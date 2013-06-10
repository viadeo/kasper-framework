// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.query.exposition.NullSafeTypeAdapter.nullSafe;

public class QueryFactoryBuilder {
	private ConcurrentMap<Type, ITypeAdapter<?>> adapters = Maps.newConcurrentMap();
	private ConcurrentMap<Type, BeanAdapter<?>> beanAdapters = Maps.newConcurrentMap();
	private List<ITypeAdapterFactory<?>> factories = Lists.newArrayList();
	private VisibilityFilter visibilityFilter = VisibilityFilter.PACKAGE_PUBLIC;

	// ------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	public QueryFactoryBuilder use(final ITypeAdapter<?> adapter) {
		checkNotNull(adapter);

		final TypeToken<?> adapterForType = TypeToken.of(adapter.getClass())
				.getSupertype(ITypeAdapter.class)
				.resolveType(ITypeAdapter.class.getTypeParameters()[0]);

		adapters.putIfAbsent(adapterForType.getType(), new NullSafeTypeAdapter<>(
				(ITypeAdapter<Object>) adapter));

		return this;
	}

	public QueryFactoryBuilder use(final ITypeAdapterFactory<?> factory) {
		factories.add(checkNotNull(factory));
		return this;
	}
	
	public QueryFactoryBuilder use(final BeanAdapter<?> beanAdapter) {
	    checkNotNull(beanAdapter);
	    
	    final TypeToken<?> adapterForType = TypeToken.of(beanAdapter.getClass())
                .getSupertype(BeanAdapter.class)
                .resolveType(BeanAdapter.class.getTypeParameters()[0]);

        beanAdapters.putIfAbsent(adapterForType.getType(), beanAdapter);

        return this;
	}

	public QueryFactoryBuilder include(final VisibilityFilter visibility) {
		this.visibilityFilter = checkNotNull(visibility);
		return this;
	}

	public IQueryFactory create() {
		for (ITypeAdapter<?> adapter : loadServices(ITypeAdapter.class)) {
			use(adapter);
        }
		
		for (BeanAdapter<?> beanAdapter : loadServices(BeanAdapter.class)) {
            use(beanAdapter);
        }
		
		for (ITypeAdapterFactory<?> adapterFactory : loadServices(ITypeAdapterFactory.class)) {
            use(adapterFactory);
        }

		adapters.putIfAbsent(int.class, nullSafe(DefaultTypeAdapters.INT_ADAPTER));
		adapters.putIfAbsent(Integer.class, nullSafe(DefaultTypeAdapters.INT_ADAPTER));
		adapters.putIfAbsent(long.class, nullSafe(DefaultTypeAdapters.Long_ADAPTER));
		adapters.putIfAbsent(Long.class, nullSafe(DefaultTypeAdapters.Long_ADAPTER));
		adapters.putIfAbsent(double.class, nullSafe(DefaultTypeAdapters.DOUBLE_ADAPTER));
		adapters.putIfAbsent(Double.class, nullSafe(DefaultTypeAdapters.DOUBLE_ADAPTER));
		adapters.putIfAbsent(float.class, nullSafe(DefaultTypeAdapters.FLOAT_ADAPTER));
		adapters.putIfAbsent(Float.class, nullSafe(DefaultTypeAdapters.FLOAT_ADAPTER));
		adapters.putIfAbsent(short.class, nullSafe(DefaultTypeAdapters.SHORT_ADAPTER));
		adapters.putIfAbsent(Short.class, nullSafe(DefaultTypeAdapters.SHORT_ADAPTER));
		
		adapters.putIfAbsent(String.class, nullSafe(DefaultTypeAdapters.STRING_ADAPTER));
		adapters.putIfAbsent(Boolean.class, nullSafe(DefaultTypeAdapters.BOOLEAN_ADAPTER));
		adapters.putIfAbsent(boolean.class, nullSafe(DefaultTypeAdapters.BOOLEAN_ADAPTER));
		adapters.putIfAbsent(Date.class, nullSafe(DefaultTypeAdapters.DATE_ADAPTER));
		adapters.putIfAbsent(DateTime.class, nullSafe(DefaultTypeAdapters.DATETIME_ADAPTER));

		factories.add(DefaultTypeAdapters.COLLECTION_ADAPTER_FACTORY);
		factories.add(DefaultTypeAdapters.ARRAY_ADAPTER_FACTORY);
		factories.add(DefaultTypeAdapters.ENUM_ADAPTER_FACTORY);

		return new StdQueryFactory(adapters, beanAdapters, factories, visibilityFilter);
	}
	
	@VisibleForTesting
	<T> List<T> loadServices(Class<T> serviceClass) {
	    final ServiceLoader<T> serviceLoader = ServiceLoader.load(
	            serviceClass, serviceClass.getClassLoader());
        return Lists.newArrayList(serviceLoader.iterator());
	}
}
