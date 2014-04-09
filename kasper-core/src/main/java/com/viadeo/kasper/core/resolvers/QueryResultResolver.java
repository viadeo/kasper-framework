// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.core.locators.QueryHandlersLocator;
import com.viadeo.kasper.cqrs.query.CollectionQueryResult;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryResult;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class QueryResultResolver extends AbstractResolver<QueryResult> {

    private static ConcurrentMap<Class, Class> cacheElements = Maps.newConcurrentMap();

    private QueryHandlersLocator queryHandlersLocator;
    private QueryHandlerResolver queryHandlerResolver;

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "QueryResult";
    }

    // ------------------------------------------------------------------------

    public Class<? extends QueryResult> getElementClass(final Class<? extends CollectionQueryResult> clazz) {

        if (cacheElements.containsKey(clazz)) {
            return cacheElements.get(clazz);
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends QueryResult>> elementClass =
                (Optional<Class<? extends QueryResult>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz,
                                CollectionQueryResult.class,
                                CollectionQueryResult.PARAMETER_RESULT_POSITION
                        );

        if ( ! elementClass.isPresent()) {
            throw new KasperException("Unable to find command type for handler " + clazz.getClass());
        }

        cacheElements.put(clazz, elementClass.get());

        return elementClass.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends QueryResult> clazz) {

        if (DOMAINS_CACHE.containsKey(checkNotNull(clazz))) {
            return Optional.<Class<? extends Domain>>of(DOMAINS_CACHE.get(clazz));
        }

        Optional<Class<? extends Domain>> result = Optional.absent();

        if (null != queryHandlersLocator) {
            final Collection<QueryHandler> queryHandlers =
                    this.queryHandlersLocator.getHandlersFromQueryResultClass(clazz);

            for (final QueryHandler queryHandler : queryHandlers) {

                final Optional<Class<? extends Domain>> domain =
                        this.queryHandlerResolver.getDomainClass(queryHandler.getClass());

                if (domain.isPresent()) {
                    if (result.isPresent()) {
                        throw new KasperException("More than one domain found");
                    }
                    result = domain;
                }

            }
        } else {
            result = domainResolver.getDomainClassOf(clazz);
        }
        
        if (result.isPresent()) {
            DOMAINS_CACHE.put(clazz, result.get());
        } 
    
        return result;
    }

    // ------------------------------------------------------------------------

    @Override
    public String getDescription(final Class<? extends QueryResult> clazz) {
        final XKasperQueryResult annotation =
                checkNotNull(clazz).getAnnotation(XKasperQueryResult.class);

        String description = "";

        if (null != annotation) {
            description = annotation.description();
        }

        if (description.isEmpty()) {
            description = String.format("The %s query answer", this.getLabel(clazz));
        }

        return description;
    }

    @Override
    public String getLabel(final Class<? extends QueryResult> clazz) {
        return checkNotNull(clazz).getSimpleName()
                .replace("Result", "")
                .replace("QueryResult", "");
    }

    // ------------------------------------------------------------------------

    public void setQueryHandlersLocator(final QueryHandlersLocator queryHandlersLocator) {
        this.queryHandlersLocator = checkNotNull(queryHandlersLocator);
    }

    public void setQueryHandlerResolver(final QueryHandlerResolver queryHandlerResolver) {
        this.queryHandlerResolver = checkNotNull(queryHandlerResolver);
    }

}
