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
import com.viadeo.kasper.cqrs.RequestActor;
import com.viadeo.kasper.cqrs.RequestActorsChain;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.cqrs.query.cache.impl.AnnotationQueryCacheActorFactory;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.impl.QueryFiltersActor;
import com.viadeo.kasper.cqrs.query.impl.QueryHandlerActor;
import com.viadeo.kasper.cqrs.query.validation.QueryValidationActor;
import com.viadeo.kasper.ddd.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.ValidationException;
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

    private static final Collection<QueryHandlerFilter> EMPTY_FILTERS =
            unmodifiableCollection(new ArrayList<QueryHandlerFilter>());

    /**
     * Registered handlers and filters
     */
    @SuppressWarnings("rawtypes")
    private final ClassToInstanceMap<QueryHandler> handlers = MutableClassToInstanceMap.create();
    private final ClassToInstanceMap<QueryHandlerFilter> filters = MutableClassToInstanceMap.create();
    private final Map<Class<? extends QueryHandler>, Class<? extends Domain>> handlerDomains = Maps.newHashMap();

    /**
     * Global filters *
     */
    private final List<Class<? extends QueryHandlerFilter>> globalFilters = Lists.newArrayList();

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
     * Association of filters per handler and domains *
     */
    private final Map<Class<? extends QueryHandler>, List<Class<? extends QueryHandlerFilter>>> appliedFilters = newHashMap();
    private final Map<Class<? extends QueryHandler>, List<QueryHandlerFilter>> instanceFilters = newHashMap();
    private final Map<Class<? extends QueryHandlerFilter>, Class<? extends Domain>> isDomainSticky = Maps.newHashMap();

    /**
     * The factory for caches
     */
    private final AnnotationQueryCacheActorFactory queryCacheFactory;
    private final Map<Class<? extends Query>, RequestActorsChain<? extends Query, ? extends QueryResponse>> requestActorChainCache = newHashMap();
    private final QueryHandlerResolver queryHandlerResolver;

    // ------------------------------------------------------------------------

    public DefaultQueryHandlersLocator() {
        this(new QueryHandlerResolver(new DomainResolver()), new AnnotationQueryCacheActorFactory());
    }

    public DefaultQueryHandlersLocator(QueryHandlerResolver queryHandlerResolver) {
        this(queryHandlerResolver, new AnnotationQueryCacheActorFactory());
    }

    public DefaultQueryHandlersLocator(QueryHandlerResolver queryHandlerResolver, final AnnotationQueryCacheActorFactory queryCacheFactory) {
        this.queryCacheFactory = queryCacheFactory;
        this.queryHandlerResolver = queryHandlerResolver;
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("rawtypes")
    @Override
    public void registerHandler(final String name, final QueryHandler handler, final Class<? extends Domain> domainClass) {
        checkNotNull(name);
        checkNotNull(handler);

        if (name.isEmpty()) {
            throw new KasperQueryException("Name of query handlers cannot be empty : " + handler.getClass());
        }

        final Class<? extends QueryHandler> handlerClass = handler.getClass();

        @SuppressWarnings("unchecked") // Safe
        final Class<? extends Query> optQueryClass = queryHandlerResolver.getQueryClass(handlerClass);
        final Class<? extends Query> queryClass = optQueryClass;
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

    /* Filter name is not currently used in the locator */
    @Override
    public void registerFilter(final String name, final QueryHandlerFilter queryFilter, final boolean isGlobal, final Class<? extends Domain> stickyDomainClass) {
        checkNotNull(name);
        checkNotNull(queryFilter);

        if (name.isEmpty()) {
            throw new KasperQueryException("Name of query handler filters cannot be empty : " + queryFilter.getClass());
        }

        final Class<? extends QueryHandlerFilter> filterClass = queryFilter.getClass();
        this.filters.put(filterClass, queryFilter);

        if (isGlobal) {
            this.globalFilters.add(filterClass);
            this.instanceFilters.clear(); // Drop all handler instances caches
            if (null != stickyDomainClass) {
                this.isDomainSticky.put(queryFilter.getClass(), stickyDomainClass);
            }
        }

    }

    @Override
    public void registerFilter(final String name, final QueryHandlerFilter queryFilter, boolean isGlobal) {
        this.registerFilter(name, queryFilter, isGlobal, null);
    }

    @Override
    public void registerFilter(final String name, final QueryHandlerFilter queryFilter) {
        this.registerFilter(name, queryFilter, false, null);
    }

    // ------------------------------------------------------------------------

    @Override
    public void registerFilterForQueryHandler(final Class<? extends QueryHandler> queryHandlerClass, final Class<? extends QueryHandlerFilter> filterClass) {
        checkNotNull(queryHandlerClass);
        checkNotNull(filterClass);

        final List<Class<? extends QueryHandlerFilter>> handlerFilters;

        if (!this.appliedFilters.containsKey(queryHandlerClass)) {
            handlerFilters = newArrayList();
            this.appliedFilters.put(queryHandlerClass, handlerFilters);
        } else if (!this.appliedFilters.get(queryHandlerClass).contains(filterClass)) {
            handlerFilters = this.appliedFilters.get(queryHandlerClass);
        } else {
            handlerFilters = null;
        }

        if (null != handlerFilters) {
            handlerFilters.add(filterClass);
            this.instanceFilters.remove(queryHandlerClass); // Drop cache of instances
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
    @SuppressWarnings("rawtypes")
    public Optional<QueryHandler> getHandlerFromQueryClass(final Class<? extends Query> queryClass) {
        final QueryHandler handler = this.handlerQueryClasses.get(queryClass);
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
    @SuppressWarnings("unchecked")
    public <Q extends Query, P extends QueryResult, R extends QueryResponse<P>>
    Optional<RequestActorsChain<Q, R>> getRequestActorChain(Class<? extends Q> queryClass) {
        RequestActorsChain<Q, R> chain = (RequestActorsChain<Q, R>) requestActorChainCache.get(queryClass);

        if (chain == null) {
            final Optional<QueryHandler> optionalQS = getHandlerFromQueryClass(queryClass);

            if (optionalQS.isPresent()) {
                final QueryHandler<Q, P> qs = optionalQS.get();
                final Class<? extends QueryHandler<Q, P>> qsClass = (Class<? extends QueryHandler<Q, P>>) qs.getClass();

                final Collection<QueryHandlerFilter> handlerFilters = getFiltersForHandlerClass(qsClass);
                final List<RequestActor<Q, R>> requestActors = Lists.newArrayList();

                /* Add cache actor if required */
                final Optional<RequestActor<Q, R>> cacheActor = queryCacheFactory.make(queryClass, qsClass);
                if (cacheActor.isPresent()) {
                    requestActors.add((RequestActor<Q, R>) queryCacheFactory.make(queryClass, qsClass).get());
                }

                /* Add validation filter */
                try {
                    requestActors.add(new QueryValidationActor(Validation.buildDefaultValidatorFactory()));
                } catch (final ValidationException ve) {
                    LOGGER.warn("No implementation found for BEAN VALIDATION - JSR 303", ve);
                }

                /* Add filters actor */
                requestActors.add(filtersActor(handlerFilters));

                /* Add handler actor */
                requestActors.add(new QueryHandlerActor(qs));

                /* Finally build the actors chan */
                chain = RequestActorsChain.makeChain(requestActors);
            }

            requestActorChainCache.put(queryClass, chain);
        }

        return Optional.fromNullable(chain);
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    private QueryFiltersActor filtersActor(final Collection<QueryHandlerFilter> handlerFilters) {

        final Collection<QueryFilter> queryFilters =
                Lists.newArrayList(Iterables.filter(handlerFilters, QueryFilter.class));

        final Collection<ResponseFilter> responseFilters =
                Lists.newArrayList(Iterables.filter(handlerFilters, ResponseFilter.class));

        return new QueryFiltersActor(queryFilters, responseFilters);
    }

    @Override
    public Collection<QueryHandler> getHandlers() {
        return unmodifiableCollection(this.handlerQueryClasses.values());
    }

    // ------------------------------------------------------------------------

    @Override
    public Collection<QueryHandlerFilter> getFiltersForHandlerClass(final Class<? extends QueryHandler> handlerClass) {

        // Ensure handler has filters
        if (!this.appliedFilters.containsKey(handlerClass) && this.globalFilters.isEmpty()) {
            return EMPTY_FILTERS;
        }


        // Ensure instances has been collected, lazy loading
        if (!this.instanceFilters.containsKey(handlerClass)) {
            List<Class<? extends QueryHandlerFilter>> filtersToApply = this.appliedFilters.get(handlerClass);

            if (null == filtersToApply) {
                filtersToApply = Lists.newArrayList();
            }

            // Apply required global filters
            for (final Class<? extends QueryHandlerFilter> globalFilterClass : this.globalFilters) {
                if (this.isDomainSticky.containsKey(globalFilterClass)) {
                    final Class<? extends Domain> stickyDomainClass = this.isDomainSticky.get(globalFilterClass);
                    if ((null != stickyDomainClass) && stickyDomainClass.equals(this.handlerDomains.get(handlerClass))) {
                        filtersToApply.add(globalFilterClass);
                    }
                } else {
                    filtersToApply.add(globalFilterClass);
                }
            }

            // Copy required filters instances to this handler cache
            final List<QueryHandlerFilter> instances = newArrayList();
            for (Class<? extends QueryHandlerFilter> filterClass : Sets.newHashSet(filtersToApply)) {
                if (this.filters.containsKey(filterClass)) {
                    instances.add(this.filters.get(filterClass));
                } else {
                    LOGGER.error(String.format("Query handler %s asks to be filtered, but no instance of filter %s can be found in records",
                            handlerClass, filterClass));
                }
            }
            this.instanceFilters.put(handlerClass, instances);
        }

        // Return the filter instances
        return unmodifiableCollection(this.instanceFilters.get(handlerClass));
    }

}
