// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.util.concurrent.ConcurrentMap;

public class QueryHandlerResolver extends AbstractResolver<QueryHandler> {

    private static final ConcurrentMap<Class, Class> cacheQuery = Maps.newConcurrentMap();
    private static final ConcurrentMap<Class, Class> cacheQueryResult = Maps.newConcurrentMap();

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "QueryHandler";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends QueryHandler> clazz) {

        if (cacheDomains.containsKey(clazz)) {
            return Optional.<Class<? extends Domain>>of(cacheDomains.get(clazz));
        }

        final XKasperQueryHandler annotation = clazz.getAnnotation(XKasperQueryHandler.class);

        if (null != annotation) {
            final Class<? extends Domain> domain = annotation.domain();
            cacheDomains.put(clazz, domain);
            return Optional.<Class<? extends Domain>>of(domain);
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    public String getDescription(Class<? extends QueryHandler> clazz) {
        final XKasperQueryHandler annotation = clazz.getAnnotation(XKasperQueryHandler.class);

        String description = "";
        if (null != annotation) {
            description = annotation.description();
        }
        if (description.isEmpty()) {
            description = String.format("The %s query handler", this.getLabel(clazz));
        }

        return description;
    }

    @Override
    public String getLabel(Class<? extends QueryHandler> clazz) {
        return clazz.getSimpleName()
                .replace("QueryHandler", "")
                .replace("Handler", "");
    }

    // ------------------------------------------------------------------------

    public Class<? extends Query> getQueryClass(final Class<? extends QueryHandler> clazz) {
        if (cacheQuery.containsKey(clazz)) {
            return cacheQuery.get(clazz);
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends Query>> queryClazz =
                (Optional<Class<? extends Query>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz, QueryHandler.class, QueryHandler.PARAMETER_QUERY_POSITION);

        if (!queryClazz.isPresent()) {
            throw new KasperException("Unable to find query type for query handler " + clazz.getClass());
        }

        cacheQuery.put(clazz, queryClazz.get());
        return queryClazz.get();
    }

    // ------------------------------------------------------------------------

    public Class<? extends QueryResult> getQueryResultClass(final Class<? extends QueryHandler> clazz) {
        if (cacheQueryResult.containsKey(clazz)) {
            return cacheQueryResult.get(clazz);
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends QueryResult>> queryResultClazz =
                (Optional<Class<? extends QueryResult>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz, QueryHandler.class, QueryHandler.PARAMETER_RESULT_POSITION);

        if (!queryResultClazz.isPresent()) {
            throw new KasperException("Unable to find query result type for query handler " + clazz.getClass());
        }

        cacheQueryResult.put(clazz, queryResultClazz.get());
        return queryResultClazz.get();
    }

}
