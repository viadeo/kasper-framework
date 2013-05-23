package com.viadeo.kasper.query.exposition;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentMap;

import org.joda.time.DateTime;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

public class QueryFactoryBuilder {
	private ConcurrentMap<Type, ITypeAdapter<?>> adapters = Maps
			.newConcurrentMap();
	private List<ITypeAdapterFactory<?>> factories = Lists.newArrayList();
	private VisibilityFilter visibilityFilter = VisibilityFilter.PACKAGE_PUBLIC;

	// ------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	public QueryFactoryBuilder use(final ITypeAdapter<?> adapter) {
		checkNotNull(adapter);
		TypeToken<?> adapterForType = TypeToken.of(adapter.getClass())
				.getSupertype(ITypeAdapter.class)
				.resolveType(ITypeAdapter.class.getTypeParameters()[0]);
		adapters.put(adapterForType.getType(), (ITypeAdapter<Object>) adapter);
		return this;
	}

	public QueryFactoryBuilder use(final ITypeAdapterFactory<?> factory) {
		factories.add(checkNotNull(factory));
		return this;
	}

	public QueryFactoryBuilder include(VisibilityFilter visibility) {
		this.visibilityFilter = checkNotNull(visibility);
		return this;
	}

	public IQueryFactory create() {
		for (ITypeAdapter<?> adapter : loadDeclaredAdapters())
			use(adapter);

		adapters.putIfAbsent(int.class, DefaultTypeAdapters.INT_ADAPTER);
		adapters.putIfAbsent(Integer.class, DefaultTypeAdapters.INT_ADAPTER);
		adapters.putIfAbsent(long.class, DefaultTypeAdapters.Long_ADAPTER);
		adapters.putIfAbsent(Long.class, DefaultTypeAdapters.Long_ADAPTER);
		adapters.putIfAbsent(double.class, DefaultTypeAdapters.DOUBLE_ADAPTER);
		adapters.putIfAbsent(Double.class, DefaultTypeAdapters.DOUBLE_ADAPTER);
		adapters.putIfAbsent(float.class, DefaultTypeAdapters.FLOAT_ADAPTER);
		adapters.putIfAbsent(Float.class, DefaultTypeAdapters.FLOAT_ADAPTER);
		adapters.putIfAbsent(short.class, DefaultTypeAdapters.SHORT_ADAPTER);
		adapters.putIfAbsent(Short.class, DefaultTypeAdapters.SHORT_ADAPTER);

		adapters.putIfAbsent(String.class, DefaultTypeAdapters.STRING_ADAPTER);
		adapters.putIfAbsent(Boolean.class, DefaultTypeAdapters.BOOLEAN_ADAPTER);
		adapters.putIfAbsent(boolean.class, DefaultTypeAdapters.BOOLEAN_ADAPTER);
		adapters.putIfAbsent(Date.class, DefaultTypeAdapters.DATE_ADAPTER);
		adapters.putIfAbsent(DateTime.class,
				DefaultTypeAdapters.DATETIME_ADAPTER);

		factories.add(DefaultTypeAdapters.COLLECTION_ADAPTER_FACTORY);
		factories.add(DefaultTypeAdapters.ARRAY_ADAPTER_FACTORY);
		factories.add(DefaultTypeAdapters.ENUM_ADAPTER_FACTORY);

		return new StdQueryFactory(adapters, factories, visibilityFilter);
	}

	@SuppressWarnings("rawtypes")
	@VisibleForTesting
	List<ITypeAdapter> loadDeclaredAdapters() {
		ServiceLoader<ITypeAdapter> serviceLoader = ServiceLoader
				.load(ITypeAdapter.class, ITypeAdapter.class.getClassLoader());
		return Lists.newArrayList(serviceLoader.iterator());
	}

	@SuppressWarnings("rawtypes")
	@VisibleForTesting
	List<ITypeAdapterFactory> loadDeclaredTypeAdapterFactory() {
		ServiceLoader<ITypeAdapterFactory> serviceLoader = ServiceLoader
				.load(ITypeAdapterFactory.class, ITypeAdapterFactory.class.getClassLoader());
		return Lists.newArrayList(serviceLoader.iterator());
	}
}
