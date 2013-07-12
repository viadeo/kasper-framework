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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.query.exposition.NullSafeTypeAdapter.nullSafe;

public class QueryFactoryBuilder {
	private ConcurrentMap<Type, TypeAdapter<?>> adapters = Maps.newConcurrentMap();
	private ConcurrentMap<Type, BeanAdapter<?>> beanAdapters = Maps.newConcurrentMap();
	private List<TypeAdapterFactory<?>> factories = Lists.newArrayList();
	private VisibilityFilter visibilityFilter = VisibilityFilter.PACKAGE_PUBLIC;
	private List<Bundle> bundles = new ArrayList<Bundle>();
	
	// ------------------------------------------------------------------------

	public QueryFactoryBuilder bundle(Bundle... extensions) {
	    for (Bundle bundle : extensions)
	        bundles.add(bundle);
	    return this;
	}
	
	@SuppressWarnings("unchecked")
	public QueryFactoryBuilder use(final TypeAdapter<?> adapter) {
		checkNotNull(adapter);

		final TypeToken<?> adapterForType = TypeToken.of(adapter.getClass())
				.getSupertype(TypeAdapter.class)
				.resolveType(TypeAdapter.class.getTypeParameters()[0]);

		adapters.putIfAbsent(adapterForType.getType(), new NullSafeTypeAdapter<Object>(
				(TypeAdapter<Object>) adapter));

		return this;
	}

	public QueryFactoryBuilder use(final TypeAdapterFactory<?> factory) {
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

	public QueryFactory create() {
		for (TypeAdapter<?> adapter : loadServices(TypeAdapter.class)) {
			use(adapter);
        }
		
		for (BeanAdapter<?> beanAdapter : loadServices(BeanAdapter.class)) {
            use(beanAdapter);
        }
		
		for (TypeAdapterFactory<?> adapterFactory : loadServices(TypeAdapterFactory.class)) {
            use(adapterFactory);
        }
		
		// after registering user extensions (must be prefered to bundles), setup with the bundles
		for (Bundle bundle : bundles)
		    bundle.setup(this);

		// and last define default adapters
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

		return new DefaultQueryFactory(adapters, beanAdapters, factories, visibilityFilter);
	}
	
	@VisibleForTesting
	<T> List<T> loadServices(Class<T> serviceClass) {
	    final ServiceLoader<T> serviceLoader = ServiceLoader.load(
	            serviceClass, serviceClass.getClassLoader());
        return Lists.newArrayList(serviceLoader.iterator());
	}
}
