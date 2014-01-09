// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.locators.impl;

import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.viadeo.kasper.core.locators.QueryHandlersLocator;
import com.viadeo.kasper.core.resolvers.DomainResolver;
import com.viadeo.kasper.core.resolvers.QueryHandlerResolver;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryHandlerAdapter;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.ddd.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.unmodifiableCollection;

/**
 * Base implementation for query handlers locator
 */
public class DefaultQueryHandlersLocator implements QueryHandlersLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultQueryHandlersLocator.class);

    private static final Collection<QueryHandlerAdapter> EMPTY_ADAPTERS =
            unmodifiableCollection(new ArrayList<QueryHandlerAdapter>());

    /**
     * Registered handlers and adapters
     */
    @SuppressWarnings("rawtypes")
    private final ClassToInstanceMap<QueryHandler> handlers = MutableClassToInstanceMap.create();
    private final ClassToInstanceMap<QueryHandlerAdapter> adapters = MutableClassToInstanceMap.create();
    private final Map<Class<? extends QueryHandler>, Class<? extends Domain>> handlerDomains = Maps.newHashMap();

    /**
     * Global adapters *
     */
    private final List<Class<? extends QueryHandlerAdapter>> globalAdapters = Lists.newArrayList();

    /**
     * Registered query classes and associated handler instances
     */
    private final Map<Class<? extends Query>, QueryHandler> handlerQueryClasses = newHashMap();

    /**
     * Registered query answer classes and associated handler instances
     */
    private final Map<Class<? extends QueryResult>, Collection<QueryHandler>> handlerQueryResultClasses = newHashMap();

    /**
     * Registered handlers names and associated handler instances
     */
    @SuppressWarnings("rawtypes")
    private final Map<String, QueryHandler> handlerNames = newHashMap();

    /**
     * Association of adapters per handler and domains *
     */
    private final Map<Class<? extends QueryHandler>, List<Class<? extends QueryHandlerAdapter>>> appliedAdapters = newHashMap();
    private final Map<Class<? extends QueryHandler>, List<QueryHandlerAdapter>> instanceAdapters = newHashMap();
    private final Map<Class<? extends QueryHandlerAdapter>, Class<? extends Domain>> isDomainSticky = Maps.newHashMap();

    private final QueryHandlerResolver queryHandlerResolver;

    // ------------------------------------------------------------------------

    public DefaultQueryHandlersLocator() {
        this(new QueryHandlerResolver(new DomainResolver()));
    }

    public DefaultQueryHandlersLocator(final QueryHandlerResolver queryHandlerResolver) {
        this.queryHandlerResolver = queryHandlerResolver;
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("rawtypes")
    @Override
    public void registerHandler(final String name, final QueryHandler handler,
                                final Class<? extends Domain> domainClass) {
        checkNotNull(name);
        checkNotNull(handler);

        if (name.isEmpty()) {
            throw new KasperQueryException("Name of query handlers cannot be empty : " + handler.getClass());
        }

        final Class<? extends QueryHandler> handlerClass = handler.getClass();

        @SuppressWarnings("unchecked") // Safe
        final Class<? extends Query> queryClass = queryHandlerResolver.getQueryClass(handlerClass);
        if (this.handlerQueryClasses.containsKey(queryClass)) {
            throw new KasperQueryException("An handler for the same query class is already registered : " + queryClass);
        }
        this.handlerQueryClasses.put(queryClass, handler);

        @SuppressWarnings("unchecked") // Safe
        final Class<? extends QueryResult> queryResultClass = queryHandlerResolver.getQueryResultClass(handlerClass);
        Collection<QueryHandler> qaClasses = this.handlerQueryResultClasses.get(queryResultClass);
        if (qaClasses == null) {
            qaClasses = new ArrayList<>();
            this.handlerQueryResultClasses.put(queryResultClass, qaClasses);
        }
        qaClasses.add(handler);

        if (this.handlerNames.containsKey(name)) {
            throw new KasperQueryException("An handler with the same name is already registered : " + name);
        }

        this.handlerQueryClasses.put(queryClass, handler);
        this.handlerNames.put(name, handler);
        this.handlers.put(handlerClass, handler);
        this.handlerDomains.put(handlerClass, domainClass);
    }

    // ------------------------------------------------------------------------

    /* Adapter name is not currently used in the locator */
    @Override
    public void registerAdapter(final String name, final QueryHandlerAdapter adapter, final boolean isGlobal, final Class<? extends Domain> stickyDomainClass) {
        checkNotNull(name);
        checkNotNull(adapter);

        final Class<? extends QueryHandlerAdapter> adapterClass = adapter.getClass();

        if (name.isEmpty()) {
            throw new KasperQueryException("Name of adapter cannot be empty : " + adapterClass);
        }

        final QueryHandlerAdapter queryHandlerAdapter = adapters.get(adapterClass);

        if (null != queryHandlerAdapter) {
            throw new KasperQueryException(
                    "The specified adapter is already registered : "
                            + queryHandlerAdapter);
        }

        this.adapters.put(adapterClass, adapter);

        if (isGlobal) {
            this.globalAdapters.add(adapterClass);
            this.instanceAdapters.clear(); // Drop all handler instances caches
            if (null != stickyDomainClass) {
                this.isDomainSticky.put(adapter.getClass(), stickyDomainClass);
            }
        }

    }

    @Override
    public void registerAdapter(final String name,
                                final QueryHandlerAdapter adapter,
                                final boolean isGlobal) {
        this.registerAdapter(name, adapter, isGlobal, null);
    }

    @Override
    public void registerAdapter(final String name, final QueryHandlerAdapter adapter) {
        this.registerAdapter(name, adapter, false, null);
    }

    // ------------------------------------------------------------------------

    @Override
    public void registerAdapterForQueryHandler(final Class<? extends QueryHandler> queryHandlerClass,
                                               final Class<? extends QueryHandlerAdapter> adapterClass) {
        checkNotNull(queryHandlerClass);
        checkNotNull(adapterClass);

        final List<Class<? extends QueryHandlerAdapter>> handlerAdapters;

       if ( ! this.appliedAdapters.containsKey(queryHandlerClass)) {
            handlerAdapters = newArrayList();
            this.appliedAdapters.put(queryHandlerClass, handlerAdapters);
        } else if ( ! this.appliedAdapters.get(queryHandlerClass).contains(adapterClass)) {
            handlerAdapters = this.appliedAdapters.get(queryHandlerClass);
        } else {
            handlerAdapters = null;
        }

        if (null != handlerAdapters) {
            handlerAdapters.add(adapterClass);
            this.instanceAdapters.remove(queryHandlerClass); // Drop cache of instances
        }
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("rawtypes")
    @Override
    public Optional<QueryHandler> getQueryHandlerFromClass(final Class<? extends QueryHandler> handlerClass) {
        final QueryHandler handler = this.handlers.getInstance(handlerClass);
        return Optional.fromNullable(handler);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Optional<QueryHandler> getHandlerByName(final String handlerName) {
        final QueryHandler handler = this.handlerNames.get(handlerName);
        return Optional.fromNullable(handler);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<QueryHandler<Query,QueryResult>> getHandlerFromQueryClass(Class<? extends Query> queryClass) {
        final QueryHandler<Query,QueryResult> handler = this.handlerQueryClasses.get(queryClass);
        return Optional.fromNullable(handler);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Collection<QueryHandler> getHandlersFromQueryResultClass(final Class<? extends QueryResult> queryResultClass) {
        final Collection<QueryHandler> tmpHandlers = this.handlerQueryResultClasses.get(queryResultClass);
        if (tmpHandlers == null) {
            return Collections.emptyList();
        }
        return tmpHandlers;
    }

    @Override
    public Collection<QueryHandler> getHandlers() {
        return unmodifiableCollection(this.handlerQueryClasses.values());
    }

    // ------------------------------------------------------------------------

    @Override
    public Collection<QueryHandlerAdapter> getAdaptersForHandlerClass(final Class<? extends QueryHandler> handlerClass) {

       // Ensure handler has adapters
        if ( ! this.appliedAdapters.containsKey(handlerClass) && this.globalAdapters.isEmpty()) {
            return EMPTY_ADAPTERS;
        }

        // Ensure instances has been collected, lazy loading
        if ( ! this.instanceAdapters.containsKey(handlerClass)) {
            List<Class<? extends QueryHandlerAdapter>> adaptersToApply = this.appliedAdapters.get(handlerClass);

            if (null == adaptersToApply) {
                adaptersToApply = Lists.newArrayList();
            }

            // Apply required global adapters
            for (final Class<? extends QueryHandlerAdapter> globalAdapterClass : this.globalAdapters) {
                if (this.isDomainSticky.containsKey(globalAdapterClass)) {
                    final Class<? extends Domain> stickyDomainClass = this.isDomainSticky.get(globalAdapterClass);
                    if ((null != stickyDomainClass) && stickyDomainClass.equals(this.handlerDomains.get(handlerClass))) {
                        adaptersToApply.add(globalAdapterClass);
                    }
                } else {
                    adaptersToApply.add(globalAdapterClass);
                }
            }

            // Copy required adapters instances to this handler cache
            final List<QueryHandlerAdapter> instances = newArrayList();
            for (final Class<? extends QueryHandlerAdapter> adapterClass : Sets.newHashSet(adaptersToApply)) {
                if (this.adapters.containsKey(adapterClass)) {
                    instances.add(this.adapters.get(adapterClass));
                } else {
                    LOGGER.error(String.format("Query handler %s asks to be adaptered, but no instance of adapter %s can be found in records",
                            handlerClass, adapterClass));
                }
            }
            this.instanceAdapters.put(handlerClass, instances);
        }

        // Return the adapter instances
        return unmodifiableCollection(this.instanceAdapters.get(handlerClass));
    }

    @Override
    public boolean containsAdapter(final Class<? extends QueryHandlerAdapter> adapterClass) {
        return adapters.containsKey(adapterClass);
    }

}
