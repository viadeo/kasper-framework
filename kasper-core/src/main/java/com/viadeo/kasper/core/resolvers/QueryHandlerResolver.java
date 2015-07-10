// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.domain.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.api.domain.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.api.domain.Domain;
import com.viadeo.kasper.api.domain.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class QueryHandlerResolver extends AbstractResolver<QueryHandler> {

    private static final ConcurrentMap<Class, Class> QUERY_CACHE = Maps.newConcurrentMap();
    private static final ConcurrentMap<Class, Class> RESULT_CACHE = Maps.newConcurrentMap();

    // ------------------------------------------------------------------------

    public QueryHandlerResolver() {
        super();
    }

    public QueryHandlerResolver(final DomainResolver domainResolver) {
        this();
        setDomainResolver(checkNotNull(domainResolver));
    }

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "QueryHandler";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends QueryHandler> clazz) {

        if (DOMAINS_CACHE.containsKey(checkNotNull(clazz))) {
            return Optional.<Class<? extends Domain>>of(DOMAINS_CACHE.get(clazz));
        }

        final XKasperQueryHandler annotation = clazz.getAnnotation(XKasperQueryHandler.class);

        if (null != annotation) {
            final Class<? extends Domain> domain = annotation.domain();
            DOMAINS_CACHE.put(clazz, domain);
            return Optional.<Class<? extends Domain>>of(domain);
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    public String getDescription(Class<? extends QueryHandler> clazz) {
        final XKasperQueryHandler annotation =
                checkNotNull(clazz).getAnnotation(XKasperQueryHandler.class);

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
    public String getLabel(final Class<? extends QueryHandler> clazz) {
        return checkNotNull(clazz).getSimpleName()
                .replace("QueryHandler", "")
                .replace("Handler", "");
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Class<? extends Query> getQueryClass(final Class<? extends QueryHandler> clazz) {

        if (QUERY_CACHE.containsKey(checkNotNull(clazz))) {
            return QUERY_CACHE.get(clazz);
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends Query>> queryClazz =
                (Optional<Class<? extends Query>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz,
                                QueryHandler.class,
                                QueryHandler.PARAMETER_QUERY_POSITION
                        );

        if ( ! queryClazz.isPresent()) {
            throw new KasperException("Unable to find query type for query handler " + clazz.getClass());
        }

        QUERY_CACHE.put(clazz, queryClazz.get());
        return queryClazz.get();
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Class<? extends QueryResult> getQueryResultClass(final Class<? extends QueryHandler> clazz) {

        if (RESULT_CACHE.containsKey(checkNotNull(clazz))) {
            return RESULT_CACHE.get(clazz);
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends QueryResult>> queryResultClazz =
                (Optional<Class<? extends QueryResult>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz,
                                QueryHandler.class,
                                QueryHandler.PARAMETER_RESULT_POSITION
                        );

        if ( ! queryResultClazz.isPresent()) {
            throw new KasperException("Unable to find query result type for query handler " + clazz.getClass());
        }

        RESULT_CACHE.put(clazz, queryResultClazz.get());
        return queryResultClazz.get();
    }

}
